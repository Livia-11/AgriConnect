package rw.agriconnect.dto;

import lombok.Data;
import rw.agriconnect.model.Role;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
} 