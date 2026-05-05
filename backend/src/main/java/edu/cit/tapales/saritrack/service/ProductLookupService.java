package edu.cit.tapales.saritrack.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class ProductLookupService {

    private final RestTemplate restTemplate = new RestTemplate();

    @org.springframework.beans.factory.annotation.Value("${upcitemdb.api.key:}")
    private String upcApiKey;

    public String lookupProductName(String barcode) {
        // 1. TRY OPEN FOOD FACTS FIRST (Great for Food/Oishi)
        String name = tryOpenFoodFacts(barcode);
        
        // 2. FALLBACK TO UPCITEMDB (Great for Hygiene/Tobacco/Safeguard)
        if ("Unknown Product".equals(name) || name == null) {
            System.out.println("--- Falling back to UPCItemDB for barcode: " + barcode + " ---");
            name = tryUPCItemDB(barcode);
        }

        return name;
    }

    @SuppressWarnings("unchecked")
    private String tryOpenFoodFacts(String barcode) {
        String url = "https://world.openfoodfacts.org/api/v2/product/" + barcode + ".json";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("product")) {
                Map<String, Object> product = (Map<String, Object>) response.get("product");
                if (product.containsKey("product_name")) {
                    return (String) product.get("product_name");
                }
            }
        } catch (Exception e) {
            System.err.println("--- OPEN FOOD FACTS FAILED: " + e.getMessage() + " ---");
        }
        return "Unknown Product";
    }

    @SuppressWarnings("unchecked")
    private String tryUPCItemDB(String barcode) {
        String url = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + barcode;
        try {
            System.out.println("--- UPCITEMDB: Requesting " + url + " ---");
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null) {
                System.out.println("--- UPCITEMDB: Received null response ---");
                return "Unknown Product";
            }

            if (response.containsKey("items")) {
                java.util.List<Map<String, Object>> items = (java.util.List<Map<String, Object>>) response.get("items");
                if (!items.isEmpty()) {
                    String title = (String) items.get(0).get("title");
                    System.out.println("--- UPCITEMDB SUCCESS: Found '" + title + "' ---");
                    return title;
                } else {
                    System.out.println("--- UPCITEMDB: Product not found in their database ---");
                }
            }
        } catch (Exception e) {
            System.err.println("--- UPCITEMDB ERROR: " + e.getMessage() + " ---");
        }
        return "Unknown Product";
    }
}
