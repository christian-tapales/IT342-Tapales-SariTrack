package edu.cit.tapales.saritrack.core.security;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

import edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF for React
                .csrf(csrf -> csrf.disable())

                // 1.5 Set Session Policy
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

                // 2. Configure CORS
                .cors(cors -> cors.configurationSource(request -> {
                    org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    // Explicitly list allowed origins to support setAllowCredentials(true)
                    corsConfiguration.setAllowedOriginPatterns(List.of(
                        "http://localhost:5173", 
                        "http://localhost:5174", 
                        "https://*.ngrok-free.app",
                        "https://*.ngrok-free.dev"
                    ));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "ngrok-skip-browser-warning"));
                    corsConfiguration.setAllowCredentials(true);
                    
                    org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", corsConfiguration);
                    return source.getCorsConfiguration(request);
                }))

                // 3. Define URLs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.web.cors.CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/", "/api/auth/**", "/api/webhooks/**", "/api/payments/**", "/login/**", "/oauth2/**", "/oauth2/authorization/**", "/error").permitAll()
                        .anyRequest().authenticated())

                // 4. ADD JWT FILTER
                .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

                // 5. PREVENT REDIRECTS FOR API CALLS (Return 401 instead of 302)
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

                // 6. ENABLE GOOGLE LOGIN
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler((request, response, authentication) -> {
                            var oAuth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication
                                    .getPrincipal();

                            String name = oAuth2User.getAttribute("name");
                            String email = oAuth2User.getAttribute("email");

                            if (name == null) name = "Google User";
                            if (email == null) email = "unknown@google.com";

                            // Sync User & Generate JWT
                            var userOpt = userRepository.findByEmail(email);
                            Long userId = null;
                            String role = "VENDOR";
                            if (userOpt.isPresent()) {
                                userId = userOpt.get().getId();
                                role = userOpt.get().getRole();
                            }

                            // ISSUE JWT PASSPORT
                            String token = jwtUtils.generateToken(email);

                            String origin = request.getHeader("Origin");
                            String baseUrl = (origin != null && origin.contains("5174")) ? "http://localhost:5174" : "http://localhost:5173";

                            String redirectUrl = baseUrl + "/dashboard?loginSuccess=true"
                                    + "&id=" + userId
                                    + "&name=" + java.net.URLEncoder.encode(name, "UTF-8")
                                    + "&token=" + token
                                    + "&role=" + role;

                            response.sendRedirect(redirectUrl);
                        }));

        return http.build();
    }
}