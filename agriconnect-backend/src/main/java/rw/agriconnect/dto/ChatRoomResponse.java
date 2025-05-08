package rw.agriconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.agriconnect.model.ChatRoom;
import rw.agriconnect.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {
    private String chatId;
    private UserResponse farmer;
    private UserResponse buyer;
    private LocalDateTime lastMessageAt;
    private boolean isActive;
    private long unreadCount;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }

    public static ChatRoomResponse fromChatRoom(ChatRoom chatRoom, long unreadCount) {
        return ChatRoomResponse.builder()
                .chatId(chatRoom.getChatId())
                .farmer(UserResponse.builder()
                        .id(chatRoom.getFarmer().getId())
                        .firstName(chatRoom.getFarmer().getFirstName())
                        .lastName(chatRoom.getFarmer().getLastName())
                        .email(chatRoom.getFarmer().getEmail())
                        .build())
                .buyer(UserResponse.builder()
                        .id(chatRoom.getBuyer().getId())
                        .firstName(chatRoom.getBuyer().getFirstName())
                        .lastName(chatRoom.getBuyer().getLastName())
                        .email(chatRoom.getBuyer().getEmail())
                        .build())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .isActive(chatRoom.isActive())
                .unreadCount(unreadCount)
                .build();
    }
} 