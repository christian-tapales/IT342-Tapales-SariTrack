package edu.cit.tapales.saritrack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

import edu.cit.tapales.saritrack.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // 1. Disable CSRF for React
        .csrf(csrf -> csrf.disable())
        
        // 2. Configure CORS
        .cors(cors -> cors.configurationSource(request -> {
            var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
            corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true); // Needed for OAuth sessions
            return corsConfiguration;
        }))
        
        // 3. Define URLs (Added OAuth2 matches)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/api/auth/**", "/login/**", "/oauth2/**", "/oauth2/authorization/**").permitAll()
            .requestMatchers("/api/products/**", "/api/orders/**", "/api/customers/**").permitAll()
            .anyRequest().authenticated()
        )

        // 4. ENABLE GOOGLE LOGIN
        .oauth2Login(oauth2 -> oauth2
    .userInfoEndpoint(userInfo -> userInfo
        .userService(customOAuth2UserService)
    )
    .successHandler((request, response, authentication) -> {
        var oAuth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
        
        // Extract real data from Google
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");

        // Construct redirect URL with real parameters
        String redirectUrl = "http://localhost:5173/dashboard?loginSuccess=true"
                + "&name=" + java.net.URLEncoder.encode(name, "UTF-8")
                + "&email=" + java.net.URLEncoder.encode(email, "UTF-8");

        response.sendRedirect(redirectUrl);
    })
    );

    return http.build();
}
}