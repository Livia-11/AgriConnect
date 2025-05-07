package rw.agriconnect.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.agriconnect.exception.ResourceNotFoundException;
import rw.agriconnect.model.*;
import rw.agriconnect.repository.OrderItemRepository;
import rw.agriconnect.repository.OrderRepository;
import rw.agriconnect.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    /**
     * Create a new order from the user's cart
     */
    @Transactional
    public Order createOrderFromCart(User buyer, Order.DeliveryMethod deliveryMethod, 
                                     String deliveryAddress, String contactPhone, String notes) {
        // Get the user's cart
        Cart cart = cartService.getUserCart(buyer);
        
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }
        
        // Create a new order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setDeliveryMethod(deliveryMethod);
        order.setDeliveryAddress(deliveryAddress);
        order.setContactPhone(contactPhone);
        order.setNotes(notes);
        order.setTotalAmount(0.0); // Will be calculated when adding items
        
        // Save the order first to get an ID
        order = orderRepository.save(order);
        
        // Convert cart items to order items
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            // Check if we have enough stock
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getTitle());
            }
            
            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setProductName(product.getTitle());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItem.setFarmer(product.getFarmer());
            
            // Update product stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            
            // Check if product stock is low after this order
            if (product.getQuantity() <= 5) {
                notificationService.notifyLowStock(product);
            }
            
            // Add item to order
            order.addItem(orderItem);
            orderItemRepository.save(orderItem);
        }
        
        // Clear the user's cart
        cartService.clearCart(buyer);
        
        // Save the updated order with items
        order = orderRepository.save(order);
        
        // Send notifications to farmers
        notificationService.notifyOrderPlaced(order);
        
        return order;
    }
    
    /**
     * Get orders for a buyer
     */
    public Page<Order> getBuyerOrders(User buyer, Pageable pageable) {
        return orderRepository.findByBuyer(buyer, pageable);
    }
    
    /**
     * Get recent orders for a buyer (last 5)
     */
    public List<Order> getRecentBuyerOrders(User buyer) {
        return orderRepository.findTop5ByBuyerOrderByCreatedAtDesc(buyer);
    }
    
    /**
     * Get orders by status
     */
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }
    
    /**
     * Get a specific order
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
    
    /**
     * Get a specific order by number
     */
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
    
    /**
     * Update order status
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status, User user) {
        Order order = getOrderById(orderId);
        
        // If the user is the buyer, they can only cancel the order
        if (order.getBuyer().getId().equals(user.getId()) && status != Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Buyer can only cancel their own orders");
        }
        
        // If the order is already delivered or cancelled, it can't be updated
        if (order.getStatus() == Order.OrderStatus.DELIVERED || order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update order status after delivery or cancellation");
        }
        
        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        
        // Set timestamps based on status
        if (status == Order.OrderStatus.PAID) {
            order.setPaymentDate(LocalDateTime.now());
            
            // Send payment received notification
            notificationService.notifyPaymentReceived(order);
        } else if (status == Order.OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }
        
        // Save the updated order
        order = orderRepository.save(order);
        
        // Send notification about status update
        if (oldStatus != status) {
            notificationService.notifyOrderStatusUpdated(order);
        }
        
        return order;
    }
    
    /**
     * Get order items for a specific farmer
     */
    public List<OrderItem> getOrderItemsByFarmer(User farmer) {
        return orderItemRepository.findByFarmer(farmer);
    }
    
    /**
     * Get order items for a specific farmer with a specific status
     */
    public List<OrderItem> getOrderItemsByFarmerAndStatus(User farmer, Order.OrderStatus status) {
        return orderItemRepository.findByOrderStatusAndFarmer(status, farmer);
    }
    
    /**
     * Generate a unique order number
     */
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 