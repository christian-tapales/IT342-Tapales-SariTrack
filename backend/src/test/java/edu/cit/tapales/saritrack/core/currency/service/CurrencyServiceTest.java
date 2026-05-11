package edu.cit.tapales.saritrack.core.currency.service;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyServiceTest {

    private final CurrencyService currencyService = new CurrencyService();

    @Test
    void testGetLatestRates_ReturnsPHPAsBase() {
        // Act
        Map<String, Double> rates = currencyService.getLatestRates();

        // Assert
        assertNotNull(rates);
        assertEquals(1.0, rates.get("PHP"), "PHP should always be the base rate of 1.0");
    }

    @Test
    void testGetLatestRates_ContainsMajorCurrencies() {
        // Act
        Map<String, Double> rates = currencyService.getLatestRates();

        // Assert
        assertTrue(rates.containsKey("USD"));
        assertTrue(rates.containsKey("EUR"));
        assertTrue(rates.containsKey("JPY"));
    }

    private void assertNotNull(Object obj) {
        if (obj == null) throw new AssertionError("Object is null");
    }
}
