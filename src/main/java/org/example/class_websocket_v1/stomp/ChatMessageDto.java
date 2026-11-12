package org.example.class_websocket_v1.stomp;

/**
 * 브라우저와 주고받는 메시지 한건을 JSON으로 구조화한것.
 * {"sender" : "홍길동" , "message" : "안녕"}
 * record : 필드를 선언하면 생성자 , 접근제한자 , equals , toString 자동으로 만들어주는 '불변 객체' .
 */
public record ChatMessageDto(String sender , String msg) {}
