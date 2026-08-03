package com.example.Ecomm.config;

import com.example.Ecomm.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Authentication
                        .requestMatchers("/auth/**").permitAll()

                        // Products
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // Reviews
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

                        // Health
                        .requestMatchers("/health").permitAll()

                        // Customer Coupons
                        .requestMatchers("/api/coupons/**").permitAll()

                        // Pincode Lookup & Deliverability Check
                        .requestMatchers("/api/location/pincode/**").permitAll()

                        // Profile Address Management
                        .requestMatchers("/api/profile/address/**").authenticated()

                        // CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // Orders
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/all").hasRole("ADMIN")

                        // Wishlist
                        .requestMatchers("/api/wishlist/**").hasRole("USER")

                        // Customization
                        .requestMatchers("/api/customization/**").authenticated()

                        // Everything else
                        .anyRequest().authenticated()

                )

                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}