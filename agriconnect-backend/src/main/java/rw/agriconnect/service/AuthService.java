package rw.agriconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rw.agriconnect.dto.AuthRequest;
import rw.agriconnect.dto.AuthResponse;
import rw.agriconnect.dto.RegisterRequest;
import rw.agriconnect.model.User;
import rw.agriconnect.repository.UserRepository;
import rw.agriconnect.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        try {
            // Validate request
            if (request.getUsername() == null || request.getEmail() == null || 
                request.getPassword() == null || request.getRole() == null) {
                throw new IllegalArgumentException("All fields (username, email, password, role) are required");
            }
            
            // Check if user already exists - using exists methods for better performance
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            
            // Create and save user
            var user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            
            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            
            return new AuthResponse(jwtToken, user.getEmail(), user.getUsername(), user.getRole());
        } catch (DataIntegrityViolationException e) {
            // Database constraint violation (likely duplicate email/username)
            System.err.println("Database error during registration: " + e.getMessage());
            throw new IllegalArgumentException("User with this email or username already exists");
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        
        var jwtToken = jwtService.generateToken(user);
        
        return new AuthResponse(jwtToken, user.getEmail(), user.getUsername(), user.getRole());
    }
} 