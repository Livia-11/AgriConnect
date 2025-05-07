package rw.agriconnect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller provides mock data endpoints when the database is unavailable or has issues.
 * It's meant for development and testing purposes only.
 */
@RestController
@RequestMapping("/api/mock")
public class MockController {
    
    private final Map<String, Object> mockDatabase = new HashMap<>();
    
    public MockController() {
        // Initialize with some sample data
        initMockData();
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Mock API is running");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getMockCart() {
        return ResponseEntity.ok(getMockCartData());
    }
    
    @PostMapping("/cart/items")
    public ResponseEntity<Map<String, Object>> addItemToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        
        Map<String, Object> cart = getMockCartData();
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
        
        // Find if product already exists in cart
        boolean found = false;
        for (Map<String, Object> item : items) {
            if (item.get("productId").equals(productId)) {
                // Update quantity
                int currentQty = (Integer) item.get("quantity");
                item.put("quantity", currentQty + quantity);
                
                // Update subtotal
                double price = (Double) item.get("productPrice");
                item.put("subtotal", price * (currentQty + quantity));
                
                found = true;
                break;
            }
        }
        
        // If product not found, add new item
        if (!found) {
            // Get mock product by ID
            Map<String, Object> product = getMockProductById(productId);
            if (product != null) {
                Map<String, Object> newItem = new HashMap<>();
                newItem.put("id", System.currentTimeMillis());
                newItem.put("productId", productId);
                newItem.put("productName", product.get("title"));
                newItem.put("productImage", product.get("imageUrl"));
                newItem.put("productPrice", product.get("price"));
                newItem.put("quantity", quantity);
                newItem.put("subtotal", (Double) product.get("price") * quantity);
                newItem.put("farmerName", "Mock Farmer");
                
                items.add(newItem);
            }
        }
        
        // Update cart totals
        updateCartTotals(cart);
        
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/cart/items/{productId}")
    public ResponseEntity<Map<String, Object>> removeItemFromCart(@PathVariable Long productId) {
        Map<String, Object> cart = getMockCartData();
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
        
        // Remove the item with matching productId
        items.removeIf(item -> item.get("productId").equals(productId));
        
        // Update cart totals
        updateCartTotals(cart);
        
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/cart/clear")
    public ResponseEntity<Map<String, Object>> clearCart() {
        Map<String, Object> cart = getMockCartData();
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
        
        // Clear all items
        items.clear();
        
        // Update cart totals
        updateCartTotals(cart);
        
        return ResponseEntity.ok(cart);
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        List<Map<String, Object>> products = getMockProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long productId) {
        Map<String, Object> product = getMockProductById(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/products/search")
    public ResponseEntity<List<Map<String, Object>>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category) {
        
        List<Map<String, Object>> allProducts = getMockProducts();
        List<Map<String, Object>> filteredProducts = new ArrayList<>();
        
        for (Map<String, Object> product : allProducts) {
            boolean matches = true;
            
            if (query != null && !query.isEmpty()) {
                String title = (String) product.get("title");
                String description = (String) product.get("description");
                
                if (!title.toLowerCase().contains(query.toLowerCase()) && 
                    !description.toLowerCase().contains(query.toLowerCase())) {
                    matches = false;
                }
            }
            
            if (category != null && !category.isEmpty()) {
                String productCategory = (String) product.get("category");
                if (!productCategory.equalsIgnoreCase(category)) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredProducts.add(product);
            }
        }
        
        return ResponseEntity.ok(filteredProducts);
    }
    
    // --- Private helper methods ---
    
    private void initMockData() {
        // Create mock products
        List<Map<String, Object>> products = new ArrayList<>();
        
        Map<String, Object> product1 = new HashMap<>();
        product1.put("id", 101L);
        product1.put("title", "Organic Fresh Tomatoes");
        product1.put("description", "Juicy, ripe tomatoes grown without pesticides. Perfect for salads and cooking.");
        product1.put("price", 3.99);
        product1.put("quantity", 50);
        product1.put("unit", "kg");
        product1.put("category", "Vegetables");
        product1.put("imageUrl", "https://images.unsplash.com/photo-1592924357230-4e8e563fbdae");
        
        Map<String, Object> product2 = new HashMap<>();
        product2.put("id", 102L);
        product2.put("title", "Russet Potatoes");
        product2.put("description", "Farm fresh potatoes, perfect for roasting, mashing, or frying.");
        product2.put("price", 2.49);
        product2.put("quantity", 100);
        product2.put("unit", "kg");
        product2.put("category", "Vegetables");
        product2.put("imageUrl", "https://images.unsplash.com/photo-1518977676601-b53f82aba655");
        
        Map<String, Object> product3 = new HashMap<>();
        product3.put("id", 103L);
        product3.put("title", "Fresh Gala Apples");
        product3.put("description", "Sweet and crisp apples, freshly harvested from our orchard.");
        product3.put("price", 4.99);
        product3.put("quantity", 75);
        product3.put("unit", "kg");
        product3.put("category", "Fruits");
        product3.put("imageUrl", "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6");
        
        products.add(product1);
        products.add(product2);
        products.add(product3);
        
        mockDatabase.put("products", products);
        
        // Initialize an empty cart
        Map<String, Object> cart = new HashMap<>();
        cart.put("id", 1L);
        cart.put("items", new ArrayList<>());
        cart.put("totalAmount", 0.0);
        cart.put("totalItems", 0);
        
        mockDatabase.put("cart", cart);
    }
    
    private Map<String, Object> getMockCartData() {
        return (Map<String, Object>) mockDatabase.get("cart");
    }
    
    private List<Map<String, Object>> getMockProducts() {
        return (List<Map<String, Object>>) mockDatabase.get("products");
    }
    
    private Map<String, Object> getMockProductById(Long productId) {
        List<Map<String, Object>> products = getMockProducts();
        
        for (Map<String, Object> product : products) {
            if (product.get("id").equals(productId)) {
                return product;
            }
        }
        
        return null;
    }
    
    private void updateCartTotals(Map<String, Object> cart) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
        
        double totalAmount = 0.0;
        int totalItems = 0;
        
        for (Map<String, Object> item : items) {
            totalAmount += (Double) item.get("subtotal");
            totalItems += (Integer) item.get("quantity");
        }
        
        cart.put("totalAmount", totalAmount);
        cart.put("totalItems", totalItems);
    }
} 