package com.puc.moeda.security;

import com.puc.moeda.repositories.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 🔍 LOG: Para você ver que este método SEMPRE executa
        log.debug("🔒 SecurityFilter executando para: {} {}", request.getMethod(), request.getRequestURI());
        
        String token = extractToken(request);
        
        if (token != null) {
            log.debug("✅ Token encontrado: {}...", token.substring(0, Math.min(20, token.length())));
            
            String email = tokenService.validateToken(token);
            
            if (email != null) {
                log.debug("✅ Token válido! Email extraído: {}", email);
                
                usuarioRepository.findByEmail(email).ifPresent(user -> {
                    log.debug("✅ Usuário encontrado: {} (Role: {})", user.getEmail(), user.getRole());
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            user, 
                            null, 
                            user.getAuthorities()
                        );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("✅ Authentication salva no SecurityContext!");
                });
            } else {
                log.warn("❌ Token inválido ou expirado!");
            }
        } else {
            log.trace("ℹ️ Nenhum token encontrado (pode ser endpoint público)");
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token do header Authorization
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " do início
        }
        
        return null;
    }
}