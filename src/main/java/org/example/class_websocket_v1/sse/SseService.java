package org.example.class_websocket_v1.sse;

/*
    SSE : 브라우저가 한번 연결을 열어두면 서버가 그 연결로 메시지를 계속 밀어넣는다.
    SSE도 소켓통신할때 소켓이라고 생각. 하지만 방향은 서버 --> 브라우저 단방향.
    브라우저가 별도 HTTP 요청을 보낼때는 새로운 요청 만들어줘야함.
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {
    // 연결된 탭마다 SseEmitter 하나씩 매핑됨.
    private final List<SseEmitter> emitterList = new CopyOnWriteArrayList<>();

    // 연결 유지시간 10분.
    private static final long TIMEOUT = 10 * 60 * 1000L;

    // 브라우저가 연결을 요청하면 SseEmitter 객체를 생성해서 List에 보관하고 돌려줌.
    public SseEmitter connet() {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterList.add(emitter);

        // i. 연결이 정상 종료되면) 목록에서 제거.
        emitter.onCompletion(() -> emitterList.remove(emitter));

        // ii. 시간이 지나면) 목록에서 제거하고 연결 마무리.
        emitter.onTimeout(() -> {
            emitterList.remove(emitter);
            emitter.complete();
        });

        // iii. 네트워크 오류 등으로 끊기면) 목록에서 제거.
        emitter.onError( e -> emitterList.remove(emitter));

        // 연결 확인 이벤트를 바로 브라우저측으로 한번 보내줘야 한다.
        // 첫 데이터가 나갈때 응답 헤더도 함께 전송되어 브라우저의 "onOpen"이 실행됨.
        // 아무것도 보내주지 않으면 설정한 시간(10분)이 지나면 스프링이 503 응답에러를 보내므로 처음 한번은 먼저 보내둔다.
        try { // 만약 연결안된 상태인데 통신 보내려하면 오류 --> try-catch
            emitter.send(SseEmitter.event().name("connect").data("연결 성공"));
        } catch (IOException e) {
//            throw new RuntimeException(e);
            emitterList.remove(emitter);
        }

        log.info("SSE 연결 추가 , 현재 연결 수 : {}" , emitterList.size());

        return emitter;
    }


    // 연결된 모든 브라우저에 "chat" 이벤트로 메시지를 보냄.
    public void send(String msg) {
        // SseEmitter에 연결된 모든 사용자 각각 메시지 보냄.
        for(SseEmitter e : emitterList){
            try {
                e.send(SseEmitter.event().name("chat").data(msg));
            } catch (IOException ex) {
                // 이미 끊긴 연결이면 보내기에 실패하므로 목록에서 제거.
                emitterList.remove(e);
            }

        }
    }
}
