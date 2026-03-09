package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.User;
import edu.cit.tapales.saritrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Error: Email already exists!";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash it!
        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody User loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
            .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            .map(user -> "Login successful! Welcome " + user.getName())
            .orElse("Error: Invalid credentials.");
    }
}