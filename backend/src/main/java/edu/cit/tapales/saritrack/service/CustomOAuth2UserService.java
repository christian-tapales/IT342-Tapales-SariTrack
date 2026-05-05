package edu.cit.tapales.saritrack.service;

import edu.cit.tapales.saritrack.entity.User;
import edu.cit.tapales.saritrack.repository.UserRepository;
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
    private EmailService emailService;

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
            emailService.sendEmail(
                email, 
                "Welcome to SariTrack! 🛒", 
                "Hi " + name + ",\n\nWelcome to SariTrack! Your store management system is ready. Log in to start tracking your sales and inventory.\n\nBest,\nThe SariTrack Team"
            );
        }

        return googleUser;
    }
}