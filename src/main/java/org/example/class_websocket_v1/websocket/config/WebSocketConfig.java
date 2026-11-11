package org.example.class_websocket_v1.websocket.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.class_websocket_v1.websocket.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration
@EnableWebSocket // 필수 -- 웹소켓 활성화함을 의미.
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        /*
         "/ws-chat" : 엔드포인트.
         setAllowedOrigins : 허용 주소 , 포트 지정.
         허용 출처를 지정하지 않으면 같은 출처(같은 주소와 같은 포트에서 열린 페이지)의 연결만 받음.
        */
        registry.addHandler(chatWebSocketHandler , "/ws-chat");
//                .setAllowedOrigins("*")
//                .setAllowedOrigins("http://localhost:5173")
//                .setAllowedOrigins("https://app.example.com");

    }
}
