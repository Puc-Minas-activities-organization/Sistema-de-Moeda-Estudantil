package com.puc.moeda.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize, @PostAuthorize, etc
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints públicos (não precisam de autenticação)
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastrar/aluno").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastrar/empresa").permitAll()
                        
                        // Temporário: endpoint para gerar hash
                        .requestMatchers("/api/hash/**").permitAll()
                        
                        // Swagger/OpenAPI - liberar todos os endpoints relacionados
                        .requestMatchers("/v3/api-docs/**", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        
                        // Endpoints de Admin - apenas ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // Endpoints de Professor - apenas PROFESSOR
                        .requestMatchers("/api/professor/**").hasRole("PROFESSOR")
                        
                        // Endpoints de Aluno - ADMIN ou ALUNO (controle de acesso no controller)
                        .requestMatchers("/api/aluno/**").hasAnyRole("ADMIN", "ALUNO")
                        
                        // Endpoints de Empresa - ADMIN ou EMPRESA_PARCEIRA
                        .requestMatchers("/api/empresa/**").hasAnyRole("ADMIN", "EMPRESA_PARCEIRA")
                        
                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}