package edu.cit.tapales.saritrack.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired(required = false)
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                email = jwtUtils.extractEmail(jwt);
                System.out.println("--- JWT FILTER: Found token for " + email + " ---");
            } catch (Exception e) {
                System.err.println("--- JWT FILTER ERROR: " + e.getMessage() + " ---");
            }
        } else {
            // System.out.println("--- JWT FILTER: No token found in header ---");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Validate token
            if (jwtUtils.validateToken(jwt, email)) {
                String role = jwtUtils.extractRole(jwt);
                if ((role == null || role.isBlank()) && userRepository != null) {
                    role = userRepository.findByEmail(email).map(User::getRole).orElse(null);
                }

                List<GrantedAuthority> authorities = new ArrayList<>();
                if (role != null && !role.isBlank()) {
                    String authority = role.toUpperCase().startsWith("ROLE_")
                            ? role.toUpperCase()
                            : "ROLE_" + role.toUpperCase();
                    authorities.add(new SimpleGrantedAuthority(authority));
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
