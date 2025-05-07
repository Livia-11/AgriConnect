package rw.agriconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.agriconnect.model.OrderItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private Double price;
    private Integer quantity;
    private Double subtotal;
    private Long farmerId;
    private String farmerName;
    
    public static OrderItemDTO fromOrderItem(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProductName());
        dto.setProductImage(item.getProductImageUrl());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        dto.setFarmerId(item.getFarmer().getId());
        dto.setFarmerName(item.getFarmer().getUsername());
        return dto;
    }
} 