package edu.cit.tapales.saritrack.core.currency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(currencyService, "restTemplate", restTemplate);
    }

    @Test
    void testGetLatestRates_Success_ShouldReturnApiRates() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 0.018);
        rates.put("EUR", 0.016);
        rates.put("JPY", 2.65);
        response.put("rates", rates);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        Map<String, Double> result = currencyService.getLatestRates();

        assertNotNull(result);
        assertEquals(0.018, result.get("USD"));
        assertEquals(1.0, result.get("PHP"));
    }

    @Test
    void testGetLatestRates_Caching_ShouldNotCallApiTwice() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 0.018);
        response.put("rates", rates);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        currencyService.getLatestRates(); // 1
        currencyService.getLatestRates(); // 2 (cached)

        verify(restTemplate, times(1)).getForObject(anyString(), eq(Map.class));
    }

    @ParameterizedTest
    @CsvSource({
        "100.0, USD, 1.8",
        "100.0, EUR, 1.6",
        "100.0, JPY, 265.0",
        "100.0, PHP, 100.0",
        "100.0, UNKNOWN, 100.0"
    })
    void testConvert_VariousCurrencies(double amount, String currency, double expected) {
        // Use fallback rates for testing conversion
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException());

        double result = currencyService.convert(amount, currency);
        assertEquals(expected, result, 0.001);
    }
}
