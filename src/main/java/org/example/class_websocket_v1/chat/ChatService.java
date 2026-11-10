package org.example.class_websocket_v1.chat;

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

    public List<Chat> findAll() {
        Sort asc = Sort.by(Sort.Direction.ASC, "id"); // id 기준 오름차순.
        return chatRepository.findAll(asc);
    }


    @Transactional
    public Chat save(String msg) {
        Chat chat = Chat.builder()
                .msg(msg)
                .build();
        return chatRepository.save(chat);
    }
}
