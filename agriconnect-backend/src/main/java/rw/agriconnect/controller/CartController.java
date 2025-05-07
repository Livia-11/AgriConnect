package rw.agriconnect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rw.agriconnect.dto.CartDTO;
import rw.agriconnect.dto.CartItemDTO;
import rw.agriconnect.model.Cart;
import rw.agriconnect.model.User;
import rw.agriconnect.service.CartService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping
    public ResponseEntity<?> getUserCart(@AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                System.err.println("User is null in getUserCart");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "ERROR");
                errorResponse.put("message", "Authentication required to access cart");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            System.out.println("Getting cart for user: " + user.getEmail());
            
            try {
                Cart cart = cartService.getOrCreateCart(user);
                return ResponseEntity.ok(CartDTO.fromCart(cart));
            } catch (Exception serviceEx) {
                // Fall back to a new empty cart if there was a problem getting the cart
                System.err.println("Error getting cart, creating new empty cart: " + serviceEx.getMessage());
                Cart emptyCart = new Cart();
                emptyCart.setUser(user);
                return ResponseEntity.ok(CartDTO.fromCart(emptyCart));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to get cart");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("errorType", e.getClass().getName());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/items")
    public ResponseEntity<?> addItemToCart(
            @AuthenticationPrincipal User user,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            System.out.println("Adding item to cart: productId=" + productId + ", quantity=" + quantity);
            Cart cart = cartService.addItemToCart(user, productId, quantity);
            return ResponseEntity.ok(CartDTO.fromCart(cart));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to add item to cart");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("errorType", e.getClass().getName());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PutMapping("/items/{productId}")
    public ResponseEntity<?> updateCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        try {
            Cart cart = cartService.updateCartItem(user, productId, quantity);
            return ResponseEntity.ok(CartDTO.fromCart(cart));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to update cart item");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("errorType", e.getClass().getName());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<?> removeItemFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        try {
            Cart cart = cartService.removeItemFromCart(user, productId);
            return ResponseEntity.ok(CartDTO.fromCart(cart));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to remove item from cart");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("errorType", e.getClass().getName());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal User user) {
        try {
            Cart cart = cartService.clearCart(user);
            return ResponseEntity.ok(CartDTO.fromCart(cart));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to clear cart");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("errorType", e.getClass().getName());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
} 