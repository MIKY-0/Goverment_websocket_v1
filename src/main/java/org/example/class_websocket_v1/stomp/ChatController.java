package org.example.class_websocket_v1.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    // 서버 어디서든지 특정 주소 (/sub/...)을 구독한 브라우저들에게 메시지를 보낼 수 있게 해주는 도구.
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    // 첫 화면 : 자동으로 1번방으로 이동.
    @GetMapping("/")
    public String home(){
        return "redirect:/rooms/1";
    }

    @GetMapping("/rooms/{roomId}")
    public String room(@PathVariable("roomId")Integer roomId , Model model) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("chats", chatService.findByRoomId(roomId));
        return "index";
    }

    /**
     * 채팅 메시지 수신 (STOMP)
     * 이 메서드는 URL로 불리지 않는다.
     * 브라우저는 ws://localhost:8080/ws-stomp로 연결을 한번 열어 두고
     * 이 연결 안으로 destination:/pub/chat/{1}이 적힌 SEND 프레임을 보냄.
     * 스프링이 "/pub"을 떼고 남은 "/chat/1"을 보고 메서드 찾음.
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") Integer roomId , ChatMessageDto request){
        log.info("수신 : {}번 방 , {}", roomId, request);

        // 빈 메시지는 필터.(브라우저 검사는 우회가능하므로 서버에서 확실히 검사)
        if(request.msg() == null || request.msg().isBlank())  return;

        // 이름이 비어있으면 "익명"으로 둔다.
        String sender = (request.sender() == null || request.sender().isBlank()) ? "익명" : request.sender().trim();
        Chat chat = chatService.save(roomId, sender, request.msg().trim());

        // 중요) 메시지 브로커에게 전달.
        // WebSocket에서 만들었던 직접 작성한 broadcast() 반복문이 이 한줄로 끝남.
        simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId ,
                new ChatMessageDto(chat.getSender() , chat.getMsg()));
    }

}
