package org.example.class_websocket_v1.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;

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
    public String save(@RequestParam(name = "msg") String msg) {
        chatService.save(msg);
        return "redirect:/save-form";
    }
}
