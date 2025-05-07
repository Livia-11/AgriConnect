package rw.agriconnect.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.agriconnect.exception.ResourceNotFoundException;
import rw.agriconnect.model.Cart;
import rw.agriconnect.model.CartItem;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;
import rw.agriconnect.repository.CartItemRepository;
import rw.agriconnect.repository.CartRepository;
import rw.agriconnect.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Get or create a cart for a user
     */
    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }
    
    /**
     * Add a product to the user's cart
     */
    @Transactional
    public Cart addItemToCart(User user, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        
        Cart cart = getOrCreateCart(user);
        
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }
        
        return cartRepository.save(cart);
    }
    
    /**
     * Update the quantity of an item in the cart
     */
    @Transactional
    public Cart updateCartItem(User user, Long productId, Integer quantity) {
        if (quantity <= 0) {
            return removeItemFromCart(user, productId);
        }
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
                
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));
                
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        
        return cart;
    }
    
    /**
     * Remove an item from the cart
     */
    @Transactional
    public Cart removeItemFromCart(User user, Long productId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
                
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));
                
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        
        return cartRepository.save(cart);
    }
    
    /**
     * Clear all items from the cart
     */
    @Transactional
    public Cart clearCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
                
        cart.getItems().clear();
        cartItemRepository.deleteByCart(cart);
        
        return cartRepository.save(cart);
    }
    
    /**
     * Get user's cart
     */
    public Cart getUserCart(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }
} 