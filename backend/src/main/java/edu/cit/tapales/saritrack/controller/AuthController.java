package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.config.JwtUtils;
import edu.cit.tapales.saritrack.entity.User;
import edu.cit.tapales.saritrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/vendors")
    public java.util.List<User> getAllVendors() {
        return userRepository.findAll().stream()
                .filter(user -> "VENDOR".equals(user.getRole()))
                .peek(user -> user.setPassword(null)) // Safety first
                .collect(java.util.stream.Collectors.toList());
    }
}