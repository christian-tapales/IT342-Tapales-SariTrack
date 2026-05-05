package edu.cit.tapales.saritrack.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@Service
public class CurrencyService {

    private final String API_URL = "https://api.exchangerate-api.com/v4/latest/PHP";
    private final RestTemplate restTemplate = new RestTemplate();

    // Cache the rates for 1 hour to prevent excessive API calls
    private Map<String, Double> cachedRates = null;
    private LocalDateTime lastFetched = null;

    @SuppressWarnings("unchecked")
    public Map<String, Double> getLatestRates() {
        if (cachedRates != null && lastFetched != null && lastFetched.isAfter(LocalDateTime.now().minusHours(1))) {
            return cachedRates;
        }

        try {
            System.out.println("--- FETCHING LIVE CURRENCY RATES FROM API ---");
            Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);
            if (response != null && response.containsKey("rates")) {
                Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                
                // We only need a few common ones
                Map<String, Double> filteredRates = new HashMap<>();
                filteredRates.put("USD", rates.get("USD"));
                filteredRates.put("EUR", rates.get("EUR"));
                filteredRates.put("JPY", rates.get("JPY"));
                filteredRates.put("PHP", 1.0); // Base

                cachedRates = filteredRates;
                lastFetched = LocalDateTime.now();
                return cachedRates;
            }
        } catch (Exception e) {
            System.err.println("--- FAILED TO FETCH CURRENCY RATES: " + e.getMessage() + " ---");
        }

        // Fallback rates if API fails
        return getFallbackRates();
    }

    private Map<String, Double> getFallbackRates() {
        Map<String, Double> fallback = new HashMap<>();
        fallback.put("USD", 0.018);
        fallback.put("EUR", 0.016);
        fallback.put("JPY", 2.65);
        fallback.put("PHP", 1.0);
        return fallback;
    }
}
