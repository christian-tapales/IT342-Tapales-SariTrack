package edu.cit.tapales.saritrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF so POST/DELETE requests work from React
            .csrf(csrf -> csrf.disable())
            
            // 2. Configure CORS to allow your React port
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                return corsConfiguration;
            }))
            
            // 3. Define which URLs are public
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()      // Login/Register
                .requestMatchers("/api/products/**").permitAll()  // Inventory
                .requestMatchers("/api/orders/**").permitAll()    // Sales/POS
                .requestMatchers("/api/customers/**").permitAll() // Listahan/Debtors
                .anyRequest().authenticated()                     // Everything else stays locked
            );

        return http.build();
    }
}