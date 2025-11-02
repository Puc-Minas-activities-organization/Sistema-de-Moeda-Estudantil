package com.puc.moeda.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.puc.moeda.models.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret:my-secret-key-change-in-production}")
    private String secret;

    @Value("${api.security.token.expiration:7200}") // 2 horas em segundos
    private Long expirationTime;

    /**
     * Gera um token JWT para o usuário
     */
    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            
            return JWT.create()
                    .withIssuer("moeda-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("userId", usuario.getId())
                    .withClaim("role", usuario.getRole().name())
                    .withExpiresAt(generateExpirationDate())
                    .withIssuedAt(Instant.now())
                    .sign(algorithm);
                    
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Valida o token e retorna o email do usuário
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            
            return JWT.require(algorithm)
                    .withIssuer("moeda-api")
                    .build()
                    .verify(token)
                    .getSubject();
                    
        } catch (JWTVerificationException exception) {
            return null; // Token inválido ou expirado
        }
    }

    /**
     * Extrai o email do usuário do token sem validar
     */
    public String getEmailFromToken(String token) {
        try {
            return JWT.decode(token).getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    /**
     * Extrai a role do usuário do token
     */
    public String getRoleFromToken(String token) {
        try {
            return JWT.decode(token).getClaim("role").asString();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    /**
     * Verifica se o token está expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Instant expiration = JWT.decode(token).getExpiresAt().toInstant();
            return expiration.isBefore(Instant.now());
        } catch (JWTVerificationException exception) {
            return true;
        }
    }

    /**
     * Gera a data de expiração do token
     */
    private Instant generateExpirationDate() {
        return LocalDateTime.now()
                .plusSeconds(expirationTime)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}