package rw.agriconnect.dto;

import lombok.Data;
import rw.agriconnect.model.User;

import java.time.LocalDateTime;

@Data
public class ProductResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Integer quantity;
    private String category;
    private Long farmerId;
    private String farmerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponseDTO fromProduct(rw.agriconnect.model.Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCategory(product.getCategory());
        
        User farmer = product.getFarmer();
        if (farmer != null) {
            dto.setFarmerId(farmer.getId());
            dto.setFarmerName(farmer.getUsername());
        }
        
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
} 