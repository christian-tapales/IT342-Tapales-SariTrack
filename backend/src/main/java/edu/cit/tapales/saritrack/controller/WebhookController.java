package edu.cit.tapales.saritrack.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.tapales.saritrack.entity.Payment;
import edu.cit.tapales.saritrack.repository.PaymentRepository;
import edu.cit.tapales.saritrack.service.PayMongoService;
import edu.cit.tapales.saritrack.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @Autowired
    private PayMongoService payMongoService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/paymongo")
    public ResponseEntity<String> handlePayMongoWebhook(
            @RequestHeader("Paymongo-Signature") String signature,
            @RequestBody String rawBody) {
        
        System.out.println("--- PAYMONGO WEBHOOK RECEIVED ---");

        if (!payMongoService.isSignatureValid(signature, rawBody)) {
            System.err.println("Invalid PayMongo Signature!");
            return ResponseEntity.status(401).body("Invalid signature");
        }

        try {
            JsonNode root = objectMapper.readTree(rawBody);
            String eventType = root.path("data").path("attributes").path("type").asText();
            
            if ("checkout_session.payment.paid".equals(eventType)) {
                String checkoutSessionId = root.path("data").path("attributes").path("data").path("id").asText();
                System.out.println("Payment successful for Session ID: " + checkoutSessionId);

                Optional<Payment> paymentOpt = paymentRepository.findByPaymongoId(checkoutSessionId);
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    payment.setStatus("PAID");
                    paymentRepository.save(payment);
                    
                    // NEW: Finalize the order and deduct stock
                    orderService.finalizeDigitalOrder(payment.getOrderId());
                    
                    System.out.println("Order finalized and stock deducted for Order ID: " + payment.getOrderId());
                } else {
                    System.err.println("No payment record found for session: " + checkoutSessionId);
                }
            }

            return ResponseEntity.ok("Event processed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }
}
