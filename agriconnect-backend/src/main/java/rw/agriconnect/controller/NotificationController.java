package rw.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rw.agriconnect.dto.NotificationDTO;
import rw.agriconnect.model.Notification;
import rw.agriconnect.model.User;
import rw.agriconnect.service.NotificationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<Notification> notifications = notificationService.getUserNotifications(user, pageable);
        Page<NotificationDTO> notificationDTOs = notifications.map(NotificationDTO::fromNotification);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", notificationDTOs.getContent());
        response.put("currentPage", notificationDTOs.getNumber());
        response.put("totalItems", notificationDTOs.getTotalElements());
        response.put("totalPages", notificationDTOs.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Notification> notifications = notificationService.getUnreadNotifications(user, pageable);
        Page<NotificationDTO> notificationDTOs = notifications.map(NotificationDTO::fromNotification);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", notificationDTOs.getContent());
        response.put("currentPage", notificationDTOs.getNumber());
        response.put("totalItems", notificationDTOs.getTotalElements());
        response.put("totalPages", notificationDTOs.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<NotificationDTO>> getRecentNotifications(@AuthenticationPrincipal User user) {
        List<Notification> notifications = notificationService.getRecentUnreadNotifications(user);
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(NotificationDTO::fromNotification)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notificationDTOs);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        long count = notificationService.countUnreadNotifications(user);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        Notification notification = notificationService.markAsRead(id, user);
        return ResponseEntity.ok(NotificationDTO.fromNotification(notification));
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }
} 