package rw.agriconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.agriconnect.model.Notification;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private boolean read;
    private String type;
    private String link;
    private LocalDateTime createdAt;
    
    public static NotificationDTO fromNotification(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setType(notification.getType().name());
        dto.setLink(notification.getLink());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
} 