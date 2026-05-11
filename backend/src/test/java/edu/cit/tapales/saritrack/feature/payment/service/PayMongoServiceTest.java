package edu.cit.tapales.saritrack.feature.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PayMongoServiceTest {

    private PayMongoService payMongoService;

    @BeforeEach
    void setUp() {
        payMongoService = new PayMongoService();
        ReflectionTestUtils.setField(payMongoService, "webhookSecret", "test_secret");
    }

    @Test
    void testIsSignatureValid_ReturnsFalseForInvalidSignature() {
        // Arrange
        String header = "t=12345,te=invalid_sig";
        String body = "{\"data\":{}}";

        // Act
        boolean isValid = payMongoService.isSignatureValid(header, body);

        // Assert
        assertFalse(isValid, "Should return false for invalid signature");
    }
}
