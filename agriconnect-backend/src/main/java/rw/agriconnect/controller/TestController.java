package rw.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.agriconnect.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;

    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Try to count users to test database connection
            long userCount = userRepository.count();
            
            response.put("status", "OK");
            response.put("message", "Database connection successful");
            response.put("userCount", userCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Database connection failed");
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getName());
            
            // Print the stack trace for debugging
            e.printStackTrace();
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 