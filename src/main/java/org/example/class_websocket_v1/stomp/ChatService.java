package org.example.class_websocket_v1.stomp;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRepository chatRepository;

    public List<Chat> findByRoomId(Integer roomId) {
        return chatRepository.findByRoomId(roomId);
    }


    @Transactional
    public Chat save(Integer roomId , String sender , String msg) {
        Chat chat = Chat.builder()
                .roomId(roomId)
                .sender(sender)
                .msg(msg)
                .build();
        return chatRepository.save(chat);
    }
}
