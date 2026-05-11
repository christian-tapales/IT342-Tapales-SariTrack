package edu.cit.tapales.saritrack.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void testGenerateToken_ShouldReturnNonEmptyString() {
        String token = jwtUtils.generateToken(testEmail);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractEmail_ShouldReturnCorrectEmail() {
        String token = jwtUtils.generateToken(testEmail);
        String extractedEmail = jwtUtils.extractEmail(token);
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    void testValidateToken_ValidToken_ShouldReturnTrue() {
        String token = jwtUtils.generateToken(testEmail);
        assertTrue(jwtUtils.validateToken(token, testEmail));
    }

    @Test
    void testValidateToken_InvalidEmail_ShouldReturnFalse() {
        String token = jwtUtils.generateToken(testEmail);
        assertFalse(jwtUtils.validateToken(token, "wrong@example.com"));
    }

    @Test
    void testIsTokenExpired_ShouldReturnFalseForNewToken() {
        String token = jwtUtils.generateToken(testEmail);
        // We can't easily test expiration without mocking time or shortening EXPIRATION_TIME,
        // but we can verify it's NOT expired right after creation.
        assertFalse(jwtUtils.extractExpiration(token).before(new java.util.Date()));
    }
}
