package com.lukaoniani.rating_systems.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(HttpMethod.GET, "/api/sellers", "/api/sellers/top", "/api/comments/seller/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/comments").permitAll()

                        // Game Objects endpoints
                        .requestMatchers(HttpMethod.GET, "/api/objects").permitAll() // Allow anyone to view game objects
                        .requestMatchers(HttpMethod.POST, "/api/objects").hasAuthority("SELLER") // Only sellers can create game objects
                        .requestMatchers(HttpMethod.PUT, "/api/objects/**").hasAuthority("SELLER") // Only sellers can edit game objects
                        .requestMatchers(HttpMethod.DELETE, "/api/objects/**").hasAuthority("SELLER") // Only sellers can delete game objects

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
