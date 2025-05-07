package rw.agriconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.agriconnect.model.Cart;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private Double totalAmount;
    private Integer totalItems;
    
    public static CartDTO fromCart(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setItems(cart.getItems().stream()
                .map(CartItemDTO::fromCartItem)
                .collect(Collectors.toList()));
        dto.setTotalAmount(cart.getTotalPrice());
        dto.setTotalItems(cart.getTotalItems());
        return dto;
    }
}