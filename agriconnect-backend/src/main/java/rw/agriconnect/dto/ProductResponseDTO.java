package rw.agriconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Integer quantity;
    private String unit;
    private String category;
    private String imageUrl;
    private Long farmerId;
    private String farmerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponseDTO fromProduct(Product product) {
        User farmer = product.getFarmer();
        return new ProductResponseDTO(
            product.getId(),
            product.getTitle(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity(),
            product.getUnit(),
            product.getCategory(),
            product.getImageUrl(),
            farmer != null ? farmer.getId() : null,
            farmer != null ? farmer.getUsername() : null,
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
} 