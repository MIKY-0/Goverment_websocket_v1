package org.example.class_websocket_v1.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
WebSocket 채팅 핸들러
연결이 됐을때 , 메시지가 올 때 , 연결이 닫힐 때 -- 스프링이 아래 메서드를 호출해줌.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends AbstractWebSocketHandler {
    private final ChatService chatService;

    // 메시지 규칙 접두사 (웹 브라우저에서도 맞춰줘야함)
    private static final String PREFIX_CHAT = "CHAT:";
    private static final String PREFIX_MESSAGE = "MESSAGE:";
    private static final String PREFIX_ERROR = "ERROR:";

    // 연결된 탭마다 WebSocketSession 하나
    private final List<WebSocketSession> sessionList = new CopyOnWriteArrayList<>();

    // 연결이 열렸을때(101 응답 직후 동작)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        log.info("연결 : {} , 현재 연결 수 : {}" , session.getId() , sessionList.size());
    }

    /**
     * 브라우저가 메시지를 보냈을 때 (브라우저의 socket.send())
     * 접두사를 확인해서 메시지 종류를 나눔.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // 1. "CHAT:" 으로 시작하지 않으면 규칙 위반. 위반한 사람에게만 오류 날림.
        if(!payload.startsWith(PREFIX_CHAT))  {
            sendTo(session , PREFIX_ERROR + "알 수 없는 메시지 형식입니다");
        }

        // 2. "CHAT:"을 떼고 내용만 추출.
        String msg = payload.substring(PREFIX_CHAT.length()).trim();

        // 3. 빈 메시지는 필터링.
        if(msg.isEmpty()) {
            sendTo(session , PREFIX_ERROR + "빈 메시지는 보낼 수 없습니다");
            return;
        }

        // 4. 저장한 뒤 모두에게 "MESSAGE:"을 붙여 브로드캐스팅.
        // 4-1. 저장.
        Chat chat = chatService.save(msg);

        // 4-2. 브로드캐스팅.
        broadcast(PREFIX_MESSAGE + chat.getMsg());
    }


    // 연결이 닫혔을 때 (탭닫기 , 새로고침 , 네트워크 오류 등)
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionList.remove(session);
        log.info("종료 : {} , 현재 연결 수 : {}" , session.getId() , sessionList.size());
    }


    ////////////////////////////////////////// 편의 메서드 //////////////////////////////////////////////////////

    // 메시지 발송 메서드.  한 브라우저에게만 보냄. (오류 , 알림용)
    private void sendTo(WebSocketSession target , String text){
        send(target , text);
    }

    /**
     * 실제 전송. target : 메시지를 받을 상대 탭에 WebSocket연결.
     * A와 B 두 스레드가 동시에 메시지를 보내면 C , D , E 사용자에게 동시에 메시지를 쓰려하는 상황이라면?
     * 연결 하나에는 한번에 한 메시지만 쓸 수 있으므로 동기화 처리 해줘야함.
     */
    private void send(WebSocketSession target , String text) {
        synchronized (target) {
            try{
                if(target.isOpen())  target.sendMessage(new TextMessage(text));
            }catch (IOException e) {
                log.warn("전송 실패 : {}" , target.getId());
                sessionList.remove(target);
            }
        }
    }

     // 연결된 모든 브라우저에게 보냄.
    private void broadcast(String text){
        for(WebSocketSession target : sessionList){
            send(target , text);
        }
    }

}
