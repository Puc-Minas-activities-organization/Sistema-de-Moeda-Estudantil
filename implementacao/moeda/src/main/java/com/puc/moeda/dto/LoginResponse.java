package com.puc.moeda.dto;

import com.puc.moeda.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo; // "Bearer"
    private String email;
    private Role role;
    private Long userId;
    private Long expiresIn; // Tempo de expiração em segundos
    
    public LoginResponse(String token, String email, Role role, Long userId, Long expiresIn) {
        this.token = token;
        this.tipo = "Bearer";
        this.email = email;
        this.role = role;
        this.userId = userId;
        this.expiresIn = expiresIn;
    }
}