package edu.cit.tapales.saritrack.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidToken_ShouldSetAuthentication() throws Exception {
        // Arrange
        String token = "valid-token";
        String email = "test@example.com";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.extractEmail(token)).thenReturn(email);
        when(jwtUtils.validateToken(token, email)).thenReturn(true);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoToken_ShouldNotSetAuthentication() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken_ShouldNotSetAuthentication() throws Exception {
        // Arrange
        String token = "invalid-token";
        String email = "test@example.com";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.extractEmail(token)).thenReturn(email);
        when(jwtUtils.validateToken(token, email)).thenReturn(false);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
