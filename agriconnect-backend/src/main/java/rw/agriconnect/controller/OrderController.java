package rw.agriconnect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.agriconnect.dto.*;
import rw.agriconnect.model.Order;
import rw.agriconnect.model.OrderItem;
import rw.agriconnect.model.User;
import rw.agriconnect.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderDTO> createOrder(
            @AuthenticationPrincipal User buyer,
            @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Order order = orderService.createOrderFromCart(
                buyer,
                createOrderDTO.getDeliveryMethodEnum(),
                createOrderDTO.getDeliveryAddress(),
                createOrderDTO.getContactPhone(),
                createOrderDTO.getNotes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderDTO.fromOrder(order));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Object>> getBuyerOrders(
            @AuthenticationPrincipal User buyer,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<Order> orders = orderService.getBuyerOrders(buyer, pageable);
        Page<OrderDTO> orderDTOs = orders.map(OrderDTO::fromOrder);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", orderDTOs.getContent());
        response.put("currentPage", orderDTOs.getNumber());
        response.put("totalItems", orderDTOs.getTotalElements());
        response.put("totalPages", orderDTOs.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recent")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<OrderDTO>> getRecentOrders(@AuthenticationPrincipal User buyer) {
        List<Order> orders = orderService.getRecentBuyerOrders(buyer);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(OrderDTO::fromOrder)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId, @AuthenticationPrincipal User user) {
        Order order = orderService.getOrderById(orderId);
        
        // Check if the user is authorized to view this order
        if (!order.getBuyer().getId().equals(user.getId()) && 
                !order.getItems().stream().anyMatch(item -> item.getFarmer().getId().equals(user.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(OrderDTO.fromOrder(order));
    }
    
    @GetMapping("/by-number/{orderNumber}")
    public ResponseEntity<OrderDTO> getOrderByNumber(@PathVariable String orderNumber, @AuthenticationPrincipal User user) {
        Order order = orderService.getOrderByNumber(orderNumber);
        
        // Check if the user is authorized to view this order
        if (!order.getBuyer().getId().equals(user.getId()) && 
                !order.getItems().stream().anyMatch(item -> item.getFarmer().getId().equals(user.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(OrderDTO.fromOrder(order));
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @AuthenticationPrincipal User user) {
        
        Order.OrderStatus orderStatus;
        try {
            orderStatus = Order.OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        
        Order updatedOrder = orderService.updateOrderStatus(orderId, orderStatus, user);
        return ResponseEntity.ok(OrderDTO.fromOrder(updatedOrder));
    }
    
    @GetMapping("/farmer-orders")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<OrderItemDTO>> getFarmerOrderItems(@AuthenticationPrincipal User farmer) {
        List<OrderItem> orderItems = orderService.getOrderItemsByFarmer(farmer);
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(OrderItemDTO::fromOrderItem)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderItemDTOs);
    }
    
    @GetMapping("/farmer-orders/by-status")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<OrderItemDTO>> getFarmerOrderItemsByStatus(
            @AuthenticationPrincipal User farmer,
            @RequestParam String status) {
        
        Order.OrderStatus orderStatus;
        try {
            orderStatus = Order.OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        
        List<OrderItem> orderItems = orderService.getOrderItemsByFarmerAndStatus(farmer, orderStatus);
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(OrderItemDTO::fromOrderItem)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderItemDTOs);
    }
} 