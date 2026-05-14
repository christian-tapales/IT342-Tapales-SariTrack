package edu.cit.tapales.saritrack.feature.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void testIsSignatureValid_ReturnsTrueForValidSignature() {
        // Arrange
        String timestamp = "12345";
        String body = "{\"data\":{}}";
        String payload = timestamp + "." + body;
        
        // Compute real HMAC for the test
        String computedHmac = "";
        try {
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                    "test_secret".getBytes(), "HmacSHA256");
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) sb.append(String.format("%02x", b));
            computedHmac = sb.toString();
        } catch (Exception e) {}

        String header = "t=" + timestamp + ",te=" + computedHmac;

        // Act
        boolean isValid = payMongoService.isSignatureValid(header, body);

        // Assert
        assertTrue(isValid, "Should return true for valid signature");
    }
}
