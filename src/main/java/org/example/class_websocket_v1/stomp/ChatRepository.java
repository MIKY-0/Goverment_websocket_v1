package org.example.class_websocket_v1.stomp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query("select c from Chat c where c.roomId = :roomId order by c.id asc")
    List<Chat> findByRoomId(@Param("roomId") Integer roomId);
}
