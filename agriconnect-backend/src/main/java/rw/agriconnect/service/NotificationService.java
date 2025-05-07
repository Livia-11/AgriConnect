package rw.agriconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.agriconnect.exception.ResourceNotFoundException;
import rw.agriconnect.model.Notification;
import rw.agriconnect.model.Order;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;
import rw.agriconnect.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    /**
     * Create a new notification
     */
    public Notification createNotification(User user, String title, String message, 
                                         Notification.NotificationType type, String link) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setLink(link);
        notification.setRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Get user's notifications
     */
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUser(user, pageable);
    }
    
    /**
     * Get user's unread notifications
     */
    public Page<Notification> getUnreadNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserAndReadFalse(user, pageable);
    }
    
    /**
     * Get user's recent unread notifications (top 10)
     */
    public List<Notification> getRecentUnreadNotifications(User user) {
        return notificationRepository.findTop10ByUserAndReadFalseOrderByCreatedAtDesc(user);
    }
    
    /**
     * Count user's unread notifications
     */
    public long countUnreadNotifications(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }
    
    /**
     * Mark a notification as read
     */
    public Notification markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        // Check if the notification belongs to the user
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Notification does not belong to the user");
        }
        
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
    
    /**
     * Mark all user's notifications as read
     */
    public void markAllAsRead(User user) {
        Page<Notification> unreadNotifications = notificationRepository.findByUserAndReadFalse(user, Pageable.unpaged());
        
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
    
    /**
     * Create order placed notification for the farmer
     */
    public void notifyOrderPlaced(Order order) {
        // Group order items by farmer and create notifications
        order.getItems().stream()
                .map(item -> item.getFarmer())
                .distinct()
                .forEach(farmer -> {
                    createNotification(
                            farmer,
                            "New Order Received",
                            "A buyer has placed a new order containing your products. Order #" + order.getOrderNumber(),
                            Notification.NotificationType.ORDER_PLACED,
                            "/farmer/orders/" + order.getId()
                    );
                });
    }
    
    /**
     * Create order status updated notification
     */
    public void notifyOrderStatusUpdated(Order order) {
        // Notify the buyer
        createNotification(
                order.getBuyer(),
                "Order Status Updated",
                "Your order #" + order.getOrderNumber() + " status has been updated to " + order.getStatus(),
                Notification.NotificationType.ORDER_UPDATED,
                "/buyer/orders/" + order.getId()
        );
        
        // Notify the farmers if status is relevant to them
        if (order.getStatus() == Order.OrderStatus.PAID || 
            order.getStatus() == Order.OrderStatus.PROCESSING || 
            order.getStatus() == Order.OrderStatus.SHIPPED) {
            
            order.getItems().stream()
                .map(item -> item.getFarmer())
                .distinct()
                .forEach(farmer -> {
                    createNotification(
                            farmer,
                            "Order Status Updated",
                            "Order #" + order.getOrderNumber() + " status has been updated to " + order.getStatus(),
                            Notification.NotificationType.ORDER_UPDATED,
                            "/farmer/orders/" + order.getId()
                    );
                });
        }
    }
    
    /**
     * Create low stock notification for the farmer
     */
    public void notifyLowStock(Product product) {
        createNotification(
                product.getFarmer(),
                "Low Stock Alert",
                "Your product '" + product.getTitle() + "' is running low on stock. Current quantity: " + product.getQuantity(),
                Notification.NotificationType.PRODUCT_LOW_STOCK,
                "/farmer/products/" + product.getId()
        );
    }
    
    /**
     * Create payment received notification
     */
    public void notifyPaymentReceived(Order order) {
        // Notify all farmers who have items in this order
        order.getItems().stream()
                .map(item -> item.getFarmer())
                .distinct()
                .forEach(farmer -> {
                    createNotification(
                            farmer,
                            "Payment Received",
                            "Payment has been received for order #" + order.getOrderNumber(),
                            Notification.NotificationType.PAYMENT_RECEIVED,
                            "/farmer/orders/" + order.getId()
                    );
                });
    }
} 