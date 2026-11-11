package org.example.class_websocket_v1.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;
    private final SseService sseService;
    // 채팅 목록 화면(폴링)
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("chats", chatService.findAll());
        return "index";
    }

    @GetMapping("/save-form")
    public String saveForm(Model model) {
        return "save-form";
    }


    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<Void> save(@RequestParam(name = "msg") String msg) {
        Chat chat = chatService.save(msg);
        sseService.send(msg);

        return ResponseEntity.ok().build();
    }
}
