package edu.cit.tapales.saritrack.feature.payment.controller;

import edu.cit.tapales.saritrack.feature.payment.entity.Payment;
import edu.cit.tapales.saritrack.feature.payment.repository.PaymentRepository;
import edu.cit.tapales.saritrack.feature.payment.service.PayMongoService;
import edu.cit.tapales.saritrack.feature.payment.dto.CheckoutSessionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PayMongoService payMongoService;

    @Autowired
    private PaymentRepository paymentRepository;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody Map<String, Object> payload) {
        try {
            String description = (String) payload.getOrDefault("description", "SariTrack Purchase");
            double amount = Double.parseDouble(payload.get("amount").toString());
            Long orderId = Long.parseLong(payload.get("orderId").toString());
            
            String baseUrl = (payload.containsKey("redirectUrl") && payload.get("redirectUrl") != null)
                    ? payload.get("redirectUrl").toString().replaceAll("/+$", "")
                    : (frontendUrl != null ? frontendUrl.replaceAll("/+$", "") : "http://localhost:5173");
            String successUrl = baseUrl + "/payment-success";
            String cancelUrl = baseUrl + "/payment-cancel";

            CheckoutSessionResponse response = payMongoService.createCheckoutSession(description, amount, successUrl, cancelUrl);
            
            // Save initial payment record
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(amount);
            payment.setPaymentMethod("PayMongo");
            payment.setStatus("PENDING");
            payment.setPaymongoId(response.getData().getId()); // Use Checkout Session ID
            paymentRepository.save(payment);

            return ResponseEntity.ok(Map.of("checkout_url", response.getData().getAttributes().getCheckout_url()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating PayMongo session: " + e.getMessage());
        }
    }
}
