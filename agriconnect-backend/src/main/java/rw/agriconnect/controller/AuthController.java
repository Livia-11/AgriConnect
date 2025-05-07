package rw.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rw.agriconnect.model.Role;
import rw.agriconnect.model.User;
import rw.agriconnect.service.AuthService;
import rw.agriconnect.dto.AuthRequest;
import rw.agriconnect.dto.AuthResponse;
import rw.agriconnect.dto.RegisterRequest;
import rw.agriconnect.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Handle validation errors with a more specific message
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Handle unexpected errors
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "An unexpected error occurred during registration");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Void> checkStatus() {
        // Perform some check (e.g., service or database availability)
        return ResponseEntity.ok().build(); // Returns HTTP 200 OK with nobody
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            // Handle unexpected errors
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to login");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            // Get the authentication from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // For debugging, output the authentication principal details
            System.out.println("Authentication principal: " + authentication.getPrincipal());
            System.out.println("Authentication name: " + email);
            
            // Simple response with hardcoded data to test if endpoint works
            if (email != null && !email.equals("anonymousUser")) {
                User user = userRepository.findByEmail(email)
                        .orElse(null);
                        
                if (user != null) {
                    return ResponseEntity.ok(new AuthResponse(
                            null, // Don't send token back
                            user.getEmail(),
                            user.getUsername(),
                            user.getRole()
                    ));
                }
            }
            
            // Fallback with demo data for debugging
            return ResponseEntity.ok(new AuthResponse(
                    null,
                    "user@example.com",
                    "Test User",
                    Role.BUYER
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving user: " + e.getMessage());
        }
    }
}