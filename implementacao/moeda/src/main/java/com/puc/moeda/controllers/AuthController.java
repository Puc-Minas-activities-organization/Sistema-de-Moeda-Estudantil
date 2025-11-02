package com.puc.moeda.controllers;

import com.puc.moeda.dto.LoginRequest;
import com.puc.moeda.dto.LoginResponse;
import com.puc.moeda.models.Usuario;
import com.puc.moeda.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint de login - público
     * POST /api/auth/login
     * Body: { "email": "user@email.com", "senha": "password" }
     * 
     * Retorna:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tipo": "Bearer",
     *   "email": "user@email.com",
     *   "role": "ALUNO",
     *   "userId": 1,
     *   "expiresIn": 7200
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email ou senha inválidos"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao realizar login: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para verificar token e obter dados do usuário autenticado
     * GET /api/auth/me
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("Usuário não autenticado"));
        }
        
        return ResponseEntity.ok(new UserInfoResponse(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getRole()
        ));
    }

    /**
     * Endpoint para validar token
     * GET /api/auth/validate
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("Token inválido ou expirado"));
        }
        
        return ResponseEntity.ok(new ValidationResponse(true, "Token válido"));
    }

    // Classes internas para responses
    record ErrorResponse(String message) {}
    record UserInfoResponse(Long id, String email, com.puc.moeda.models.Role role) {}
    record ValidationResponse(boolean valid, String message) {}
}