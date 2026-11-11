package org.example.class_websocket_v1.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

    /*
     -----중요(약속)------
     브라우저의 EventSource가 연결하는 주소.
     응답 타입("text/event-stream")은 브라우저가 이 응답을 "이벤트 스트림"으로 해석하게 하는 표시.
     -- MediaType.TEXT_EVENT_STREAM_VALUE 지정안해줘도 기본으로 지정됨.
     이 요청은 응답이 바로 끝나지 않고 서버가 메시지를 보낼 때까지 열린 채로 남음.
     */
    @GetMapping(value = "/sse/connect" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        return sseService.connet();
    }
}
