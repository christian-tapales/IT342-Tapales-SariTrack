package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.config.JwtUtils;
import edu.cit.tapales.saritrack.entity.User;
import edu.cit.tapales.saritrack.repository.UserRepository;
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
    private edu.cit.tapales.saritrack.service.EmailService emailService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Error: Email already exists!";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash it!
        userRepository.save(user);
        
        // Trigger Welcome Email (Premium HTML version)
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public Object login(@RequestBody User loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtils.generateToken(user.getEmail());
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    response.put("name", user.getName());
                    response.put("email", user.getEmail());
                    response.put("role", user.getRole());
                    response.put("token", token);
                    return (Object) response;
                })
                .orElse("Error: Invalid credentials.");
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

                String token = jwtUtils.generateToken(user.getEmail());
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