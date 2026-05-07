package edu.cit.tapales.saritrack.feature.auth.service;

import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private edu.cit.tapales.saritrack.feature.notification.service.EmailService emailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Fetch the user info from Google
        OAuth2User googleUser = super.loadUser(userRequest);
        
        String email = googleUser.getAttribute("email");
        String name = googleUser.getAttribute("name");

        if (name == null) name = "Google User";

        // 2. Sync with Supabase: Save user if they don't exist
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            // OAuth users don't need a password field filled, but your entity 
            // requires it. Set a random one or modify the entity.
            newUser.setPassword("OAUTH_USER"); 
            userRepository.save(newUser);

            // 3. Send Welcome Email
            try {
                emailService.sendWelcomeEmail(email, name);
            } catch (Exception e) {
                System.err.println("--- FAILED TO SEND OAUTH WELCOME EMAIL: " + e.getMessage() + " ---");
            }
        }

        return googleUser;
    }
}