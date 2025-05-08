package rw.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rw.agriconnect.model.ChatMessage;
import rw.agriconnect.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage message) {
        chatService.sendMessage(
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent()
        );
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(chatService.getChatHistory(
                Long.parseLong(userDetails.getUsername()),
                userId
        ));
    }

    @PostMapping("/read/{senderId}")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long senderId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        chatService.markMessagesAsRead(
                senderId,
                Long.parseLong(userDetails.getUsername())
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadMessageCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(chatService.getUnreadMessageCount(
                Long.parseLong(userDetails.getUsername())
        ));
    }
} 