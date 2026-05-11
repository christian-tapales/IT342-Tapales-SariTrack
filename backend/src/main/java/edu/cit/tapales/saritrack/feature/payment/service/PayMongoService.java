package edu.cit.tapales.saritrack.feature.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.tapales.saritrack.feature.payment.dto.CheckoutSessionRequest;
import edu.cit.tapales.saritrack.feature.payment.dto.CheckoutSessionResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class PayMongoService {

    @Value("${paymongo.secret.key}")
    private String secretKey;

    @Value("${paymongo.webhook.secret}")
    private String webhookSecret;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_URL = "https://api.paymongo.com/v1/checkout_sessions";

    public CheckoutSessionResponse createCheckoutSession(String description, double amount, String successUrl, String cancelUrl) throws IOException {
        int amountInCentavos = (int) Math.round(amount * 100);

        CheckoutSessionRequest requestBody = CheckoutSessionRequest.builder()
                .data(CheckoutSessionRequest.DataContainer.builder()
                        .attributes(CheckoutSessionRequest.Attributes.builder()
                                .description(description)
                                .payment_method_types(List.of("gcash", "paymaya", "card"))
                                .line_items(List.of(CheckoutSessionRequest.LineItem.builder()
                                        .name("Total Amount")
                                        .description(description)
                                        .amount(amountInCentavos)
                                        .currency("PHP")
                                        .quantity(1)
                                        .build()))
                                .success_url(successUrl)
                                .cancel_url(cancelUrl)
                                .build())
                        .build())
                .build();

        String json = objectMapper.writeValueAsString(requestBody);
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", authHeader)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + " Body: " + response.body().string());
            }
            return objectMapper.readValue(response.body().string(), CheckoutSessionResponse.class);
        }
    }

    public boolean isSignatureValid(String signatureHeader, String rawBody) {
        try {
            System.out.println("Using Webhook Secret: " + webhookSecret);
            
            String[] parts = signatureHeader.split(",");
            String timestamp = "";
            String providedSignature = "";

            for (String part : parts) {
                if (part.trim().startsWith("t=")) timestamp = part.trim().substring(2);
                if (part.trim().startsWith("te=")) providedSignature = part.trim().substring(3);
                if (part.trim().startsWith("li=")) {
                    if (providedSignature.isEmpty()) providedSignature = part.trim().substring(3);
                }
            }

            String payload = timestamp + "." + rawBody;
            
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                    webhookSecret.trim().getBytes(), "HmacSHA256");
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            
            byte[] rawHmac = mac.doFinal(payload.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) {
                sb.append(String.format("%02x", b));
            }
            String computedSignature = sb.toString();

            System.out.println("Provided Signature: " + providedSignature);
            System.out.println("Computed Signature: " + computedSignature);

            return computedSignature.equals(providedSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
