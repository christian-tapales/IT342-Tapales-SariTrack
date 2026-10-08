package edu.cit.tapales.saritrack.feature.auth.controller;

import edu.cit.tapales.saritrack.core.security.JwtUtils;
import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private edu.cit.tapales.saritrack.feature.notification.service.EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Collections.singletonMap("error", "Email already exists!"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash it!
        userRepository.save(user);
        
        // Trigger Welcome Email (Premium HTML version)
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        
        return ResponseEntity.ok(java.util.Collections.singletonMap("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtils.generateToken(user.getEmail(), user.getRole());
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    response.put("name", user.getName());
                    response.put("email", user.getEmail());
                    response.put("role", user.getRole());
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Invalid email or password")));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email is required."));
        }
        
        java.util.Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User with this email does not exist."));
        }
        
        User user = userOpt.get();
        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
        
        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset instructions have been sent to your email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Token and new password are required."));
        }
        
        java.util.Optional<User> userOpt = userRepository.findByResetToken(token.trim());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid reset token."));
        }
        
        User user = userOpt.get();
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Reset token has expired."));
        }
        
        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        
        return ResponseEntity.ok(Collections.singletonMap("message", "Password has been reset successfully. You can now log in."));
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @PostMapping("/google-mobile")
    public ResponseEntity<?> googleMobileLogin(@RequestBody Map<String, String> request) {
        String idTokenString = request.get("idToken");
        
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // Check if user exists, else create
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole("VENDOR");
                    newUser.setPassword(passwordEncoder.encode("GOOGLE_USER_" + java.util.UUID.randomUUID()));
                    return userRepository.save(newUser);
                });

                String token = jwtUtils.generateToken(user.getEmail(), user.getRole());
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("role", user.getRole());
                response.put("token", token);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Auth error: " + e.getMessage());
        }
    }

    @GetMapping("/vendors")
    public java.util.List<User> getAllVendors() {
        return userRepository.findAll().stream()
                .filter(user -> "VENDOR".equals(user.getRole()))
                .peek(user -> user.setPassword(null)) // Safety first
                .collect(java.util.stream.Collectors.toList());
    }
}