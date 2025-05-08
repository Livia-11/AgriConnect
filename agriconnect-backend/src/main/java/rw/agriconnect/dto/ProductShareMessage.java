package rw.agriconnect.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductShareMessage {
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String unit;
    private String category;
    private String farmerName;
    private Long farmerId;
} 