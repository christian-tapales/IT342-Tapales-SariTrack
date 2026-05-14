package edu.cit.tapales.saritrack.feature.auth.service;

import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import edu.cit.tapales.saritrack.feature.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private OAuth2UserRequest oAuth2UserRequest;

    @Mock
    private OAuth2User oAuth2User;

    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a subclass that overrides fetchGoogleUser to avoid real network calls
        customOAuth2UserService = new CustomOAuth2UserService() {
            @Override
            protected OAuth2User fetchGoogleUser(OAuth2UserRequest userRequest) {
                return oAuth2User;
            }
        };
        
        // Manually inject mocked dependencies
        ReflectionTestUtils.setField(customOAuth2UserService, "userRepository", userRepository);
        ReflectionTestUtils.setField(customOAuth2UserService, "emailService", emailService);
    }

    @Test
    void testLoadUser_ExistingUser_ShouldJustReturnUser() {
        // Arrange
        when(oAuth2User.getAttribute("email")).thenReturn("test@gmail.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Test Google User");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(new User()));

        // Act
        OAuth2User result = customOAuth2UserService.loadUser(oAuth2UserRequest);

        // Assert
        assertEquals(oAuth2User, result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoadUser_NewUser_ShouldSaveAndSendEmail() {
        // Arrange
        when(oAuth2User.getAttribute("email")).thenReturn("new@gmail.com");
        when(oAuth2User.getAttribute("name")).thenReturn("New User");
        when(userRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());

        // Act
        OAuth2User result = customOAuth2UserService.loadUser(oAuth2UserRequest);

        // Assert
        assertEquals(oAuth2User, result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendWelcomeEmail("new@gmail.com", "New User");
    }
}
