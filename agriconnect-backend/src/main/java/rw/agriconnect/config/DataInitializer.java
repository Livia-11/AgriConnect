package rw.agriconnect.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.Role;
import rw.agriconnect.model.User;
import rw.agriconnect.repository.ProductRepository;
import rw.agriconnect.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeData() {
        try {
            // Initialize users
            initializeUsers();
            
            // Initialize products
            initializeProducts();
            
        } catch (Exception e) {
            System.err.println("Failed to initialize data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeUsers() {
        // Check if we need to add default users
        if (userRepository.count() == 0) {
            System.out.println("No users found, creating default test users...");
                
            // Create a test FARMER user
            User farmer = new User();
            farmer.setUsername("farmer");
            farmer.setEmail("farmer@example.com");
            farmer.setPassword(passwordEncoder.encode("password123"));
            farmer.setRole(Role.FARMER);
            userRepository.save(farmer);
            System.out.println("Created default farmer user: farmer@example.com");
                
            // Create a test BUYER user
            User buyer = new User();
            buyer.setUsername("buyer");
            buyer.setEmail("buyer@example.com");
            buyer.setPassword(passwordEncoder.encode("password123"));
            buyer.setRole(Role.BUYER);
            userRepository.save(buyer);
            System.out.println("Created default buyer user: buyer@example.com");
        } else {
            System.out.println("Database already has users, skipping user initialization");
        }
    }
    
    private void initializeProducts() {
        // Check if we need to add default products
        if (productRepository.count() == 0) {
            System.out.println("No products found, creating sample products...");
            
            // Get the farmer user
            User farmer = userRepository.findByEmail("farmer@example.com").orElse(null);
            
            if (farmer != null) {
                // Create product 1: Tomatoes
                Product tomatoes = new Product();
                tomatoes.setTitle("Organic Fresh Tomatoes");
                tomatoes.setDescription("Juicy, ripe tomatoes grown without pesticides. Perfect for salads and cooking.");
                tomatoes.setPrice(3.99);
                tomatoes.setQuantity(50);
                tomatoes.setUnit("kg");
                tomatoes.setCategory("Vegetables");
                tomatoes.setImageUrl("https://images.unsplash.com/photo-1592924357230-4e8e563fbdae");
                tomatoes.setFarmer(farmer);
                productRepository.save(tomatoes);
                
                // Create product 2: Potatoes
                Product potatoes = new Product();
                potatoes.setTitle("Russet Potatoes");
                potatoes.setDescription("Farm fresh potatoes, perfect for roasting, mashing, or frying.");
                potatoes.setPrice(2.49);
                potatoes.setQuantity(100);
                potatoes.setUnit("kg");
                potatoes.setCategory("Vegetables");
                potatoes.setImageUrl("https://images.unsplash.com/photo-1518977676601-b53f82aba655");
                potatoes.setFarmer(farmer);
                productRepository.save(potatoes);
                
                // Create product 3: Apples
                Product apples = new Product();
                apples.setTitle("Fresh Gala Apples");
                apples.setDescription("Sweet and crisp apples, freshly harvested from our orchard.");
                apples.setPrice(4.99);
                apples.setQuantity(75);
                apples.setUnit("kg");
                apples.setCategory("Fruits");
                apples.setImageUrl("https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6");
                apples.setFarmer(farmer);
                productRepository.save(apples);
                
                System.out.println("Created 3 sample products");
            } else {
                System.err.println("Failed to create products: farmer user not found");
            }
        } else {
            System.out.println("Database already has products, skipping product initialization");
        }
    }
} 