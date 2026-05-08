package edu.cit.tapales.saritrack.feature.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ProductLookupServiceTest {

    private static final String DUMMY_BARCODE = "12345678";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductLookupService productLookupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(productLookupService, "restTemplate", restTemplate);
    }

    @Test
    void testLookupSuccess() {
        // Arrange
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("product_name", "Test Oishi");
        response.put("product", data);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        // Act
        String productName = productLookupService.lookupProductName(DUMMY_BARCODE);

        // Assert
        assertEquals("Test Oishi", productName);
    }

    @Test
    void testLookupFallback() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        // Act
        String productName = productLookupService.lookupProductName(DUMMY_BARCODE);

        // Assert
        assertEquals("Unknown Product", productName);
    }
}
