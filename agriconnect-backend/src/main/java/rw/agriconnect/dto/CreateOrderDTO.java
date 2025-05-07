package rw.agriconnect.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.agriconnect.model.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
    @NotNull(message = "Delivery method is required")
    private String deliveryMethod;
    
    @NotEmpty(message = "Delivery address is required")
    private String deliveryAddress;
    
    @NotEmpty(message = "Contact phone is required")
    private String contactPhone;
    
    private String notes;
    
    public Order.DeliveryMethod getDeliveryMethodEnum() {
        return Order.DeliveryMethod.valueOf(deliveryMethod);
    }
} 