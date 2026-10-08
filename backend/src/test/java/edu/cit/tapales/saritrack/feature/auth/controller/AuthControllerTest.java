package edu.cit.tapales.saritrack.feature.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.tapales.saritrack.core.security.JwtUtils;
import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import edu.cit.tapales.saritrack.feature.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for this test
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setName("Test User");
        testUser.setRole("VENDOR");
    }

    @Test
    void testRegister_Success_ShouldReturnSuccessMessage() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendWelcomeEmail(eq("test@example.com"), eq("Test User"));
    }

    @Test
    void testRegister_EmailAlreadyExists_ShouldReturnErrorMessage() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists!"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success_ShouldReturnTokenAndUserData() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(anyString())).thenReturn("mocked-jwt-token");
        when(jwtUtils.generateToken(anyString(), any())).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void testLogin_InvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    void testForgotPassword_Success() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset instructions have been sent to your email."));

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendPasswordResetEmail(eq("test@example.com"), eq("Test User"), anyString());
    }

    @Test
    void testForgotPassword_UserNotFound() throws Exception {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"notfound@example.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User with this email does not exist."));

        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testForgotPassword_MissingEmail() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is required."));
    }

    @Test
    void testResetPassword_Success() throws Exception {
        testUser.setResetToken("valid-token-123");
        testUser.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByResetToken("valid-token-123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newSecret456")).thenReturn("hashedNewSecret");

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\": \"valid-token-123\", \"newPassword\": \"newSecret456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password has been reset successfully. You can now log in."));

        verify(passwordEncoder, times(1)).encode("newSecret456");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testResetPassword_InvalidToken() throws Exception {
        when(userRepository.findByResetToken("unknown-token")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\": \"unknown-token\", \"newPassword\": \"newSecret456\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid reset token."));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testResetPassword_ExpiredToken() throws Exception {
        testUser.setResetToken("expired-token-123");
        testUser.setResetTokenExpiry(java.time.LocalDateTime.now().minusMinutes(5));
        when(userRepository.findByResetToken("expired-token-123")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\": \"expired-token-123\", \"newPassword\": \"newSecret456\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Reset token has expired."));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testResetPassword_MissingFields() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\": \"\", \"newPassword\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Token and new password are required."));
    }
}
