package org.example.class_websocket_v1.stomp.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * STOMP 설정
 * 순수 웹소켓 코드에서 직접 만들었던 웹소켓 세션과 여러 관리를 여기 메시지 브로커가 대신함.
 *
 * 주소 3가지
 * ws://localhost:8080/ws-stomp : 브라우저가 웹소켓 연결을 여는 주소.
 * /pub/... : '브라우저 TO 서버' 일때 사용.(@MessageMapping을 활용해 주소 받는다)
 * /sub/... : '서버 TO 브라우저' 일때 사용.(메시지 브로커가 이 주소를 구독한 모두에게 전송)
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker // 메시지 브로커(stomp 사용할때 필수)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 1. 연결 주소
     * 브라우저는 ws://localhost:8080/ws-stomp로 웹소켓 연결을 연다.
     * WebSocket처럼 허용 출처를 적지 않으면 같은화면에서 같은 출력을 연 주소 , 포트 연결만 받음.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("ws-stomp");
    }

    /**
     *
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // '/pub/' 으로 시작하는 메시지가 왔을 때 @MessageMapping이 붙은 컨트롤러로 메서드를 보냄.
        registry.setApplicationDestinationPrefixes("/pub");

        // '/sub' 으로 시작하는 주소는 스프링 내장(메모리) 브로커가 구독자에 알아서 전달.
        registry.enableSimpleBroker("/sub");
    }
}
