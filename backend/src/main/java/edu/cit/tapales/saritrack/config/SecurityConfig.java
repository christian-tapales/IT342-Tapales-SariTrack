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

    @Autowired
    private edu.cit.tapales.saritrack.repository.UserRepository userRepository;

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
                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))

                // 3. Define URLs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**", "/api/webhooks/**", "/api/payments/**", "/login/**", "/oauth2/**", "/oauth2/authorization/**").permitAll()
                        .requestMatchers("/api/products/**", "/api/orders/**", "/api/customers/**", "/api/vendor/dashboard/**", "/api/notifications/**").permitAll()
                        .anyRequest().authenticated())

                // 4. PREVENT REDIRECTS FOR API CALLS (Return 401 instead of 302)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            String path = request.getRequestURI();
                            System.out.println(
                                    "--- SECURITY BLOCK: Path=" + path + " Method=" + request.getMethod() + " ---");
                            if (path != null && (path.contains("/api") || path.startsWith("/api"))) {
                                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                            } else {
                                response.sendRedirect("/login");
                            }
                        }))

                // 5. ENABLE GOOGLE LOGIN
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler((request, response, authentication) -> {
                            var oAuth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication
                                    .getPrincipal();

                            // Extract real data from Google (with null safety)
                            String name = oAuth2User.getAttribute("name");
                            String email = oAuth2User.getAttribute("email");

                            if (name == null)
                                name = "Google User";
                            if (email == null)
                                email = "unknown@google.com";

                            // Fetch user from DB to get their role and ID (for Google Login)
                            String role = "VENDOR"; // Default
                            Long userId = null;
                            var userOpt = userRepository.findByEmail(email);
                            if (userOpt.isPresent()) {
                                role = userOpt.get().getRole();
                                userId = userOpt.get().getId();
                            }

                            // Determine port based on request origin (defaulting to 5173)
                            String origin = request.getHeader("Origin");
                            String baseUrl = (origin != null && origin.contains("5174")) ? "http://localhost:5174"
                                    : "http://localhost:5173";

                            // Construct redirect URL with real parameters including ID
                            String redirectUrl = baseUrl + "/dashboard?loginSuccess=true"
                                    + "&id=" + userId
                                    + "&name=" + java.net.URLEncoder.encode(name, "UTF-8")
                                    + "&email=" + java.net.URLEncoder.encode(email, "UTF-8")
                                    + "&role=" + role;

                            response.sendRedirect(redirectUrl);
                        }));

        return http.build();
    }
}