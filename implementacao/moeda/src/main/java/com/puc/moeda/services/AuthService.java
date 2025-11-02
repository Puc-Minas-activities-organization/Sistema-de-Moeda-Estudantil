package com.puc.moeda.services;

import com.puc.moeda.dto.LoginRequest;
import com.puc.moeda.dto.LoginResponse;
import com.puc.moeda.models.Usuario;
import com.puc.moeda.repositories.UsuarioRepository;
import com.puc.moeda.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${api.security.token.expiration:7200}")
    private Long expirationTime;

    public LoginResponse login(LoginRequest request) {
        try {
            // Cria o token de autenticação
            UsernamePasswordAuthenticationToken usernamePassword = 
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha());

            // Autentica o usuário
            Authentication auth = authenticationManager.authenticate(usernamePassword);

            // Obtém o usuário autenticado
            Usuario usuario = (Usuario) auth.getPrincipal();

            // Gera o token JWT
            String token = tokenService.generateToken(usuario);

            // Retorna o response com o token
            return new LoginResponse(
                token,
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getId(),
                expirationTime
            );

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Email ou senha inválidos");
        }
    }

    public Usuario getUsuarioAutenticado(String token) {
        String email = tokenService.validateToken(token);
        
        if (email == null) {
            throw new RuntimeException("Token inválido ou expirado");
        }

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}