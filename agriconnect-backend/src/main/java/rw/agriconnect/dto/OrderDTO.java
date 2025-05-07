package rw.agriconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.agriconnect.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long buyerId;
    private String buyerName;
    private List<OrderItemDTO> items;
    private Double totalAmount;
    private String status;
    private String deliveryMethod;
    private String deliveryAddress;
    private String contactPhone;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paymentDate;
    private LocalDateTime deliveryDate;
    
    public static OrderDTO fromOrder(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setBuyerId(order.getBuyer().getId());
        dto.setBuyerName(order.getBuyer().getUsername());
        dto.setItems(order.getItems().stream()
                .map(OrderItemDTO::fromOrderItem)
                .collect(Collectors.toList()));
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setDeliveryMethod(order.getDeliveryMethod().name());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setContactPhone(order.getContactPhone());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setPaymentDate(order.getPaymentDate());
        dto.setDeliveryDate(order.getDeliveryDate());
        return dto;
    }
} 