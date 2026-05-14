package edu.cit.tapales.saritrack.feature.auth.controller;

import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    // Security mocks
    @MockitoBean
    private edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean
    private edu.cit.tapales.saritrack.core.security.JwtUtils jwtUtils;
    @MockitoBean
    private edu.cit.tapales.saritrack.core.security.JwtFilter jwtFilter;

    @Test
    void testMe_Authenticated_ShouldReturnUser() throws Exception {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setName("Test User");
        
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        // Manually set an AUTHENTICATED principal
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "test@gmail.com", null, java.util.Collections.emptyList());
        
        mockMvc.perform(get("/api/auth/me")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    void testMe_Unauthenticated_ShouldReturn401() throws Exception {
        // No principal provided
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
