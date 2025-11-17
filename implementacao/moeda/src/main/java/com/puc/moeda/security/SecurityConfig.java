package com.puc.moeda.security;

import java.util.Arrays;
import java.util.List;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize, @PostAuthorize, etc
public class SecurityConfig {

  @Autowired private SecurityFilter securityFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> {}) // habilita suporte a CORS usando o CorsConfigurationSource bean
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    // Endpoints públicos (não precisam de autenticação)
                    .requestMatchers(HttpMethod.POST, "/api/auth/login")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastrar/aluno")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastrar/empresa")
                    .permitAll()

                    // Temporário: endpoint para gerar hash
                    .requestMatchers("/api/hash/**")
                    .permitAll()

                    // Swagger/OpenAPI - liberar todos os endpoints relacionados
                    .requestMatchers(
                        "/v3/api-docs/**", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()

                    // CRUD público de Aluno (sem autenticação)
                    .requestMatchers(HttpMethod.GET, "/api/aluno/todos")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/aluno/{id}")
                    .permitAll()
                    .requestMatchers(HttpMethod.PUT, "/api/aluno/{id}")
                    .permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/aluno/{id}")
                    .permitAll()

                    // CRUD público de Empresa (sem autenticação)
                    .requestMatchers(HttpMethod.GET, "/api/empresa/todas")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/empresa/{id}")
                    .permitAll()
                    .requestMatchers(HttpMethod.PUT, "/api/empresa/{id}")
                    .permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/empresa/{id}")
                    .permitAll()

                    .requestMatchers("/api/test/email/**").permitAll()

                    // Endpoints de Professor - apenas PROFESSOR
                    .requestMatchers("/api/professor/**")
                    .hasRole("PROFESSOR")

                    // Endpoints de Aluno (protegidos) - apenas ALUNO
                    .requestMatchers("/api/aluno/**")
                    .hasRole("ALUNO")

                    // Endpoints de Empresa (protegidos) - apenas EMPRESA_PARCEIRA
                    .requestMatchers("/api/empresa/**")
                    .hasRole("EMPRESA_PARCEIRA")

                    // Qualquer outra requisição precisa estar autenticada
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  /**
   * Cors configuration source para habilitar chamadas do Live Server (ex: http://127.0.0.1:5500)
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    List<String> allowedOrigins =
        Arrays.asList(
            "http://127.0.0.1:5500",
            "http://localhost:5500",
            "http://localhost:3000",
            "http://localhost:8080");
    config.setAllowedOrigins(allowedOrigins);
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
