package edu.cit.tapales.saritrack.feature.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckoutSessionResponse {
    private DataContainer data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataContainer {
        private String id;
        private Attributes attributes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {
        private String checkout_url;
        private String status;
        private String payment_intent_id;
    }
}
