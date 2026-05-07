package edu.cit.tapales.saritrack.feature.payment.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CheckoutSessionRequest {
    private DataContainer data;

    @Data
    @Builder
    public static class DataContainer {
        private Attributes attributes;
    }

    @Data
    @Builder
    public static class Attributes {
        private List<String> payment_method_types;
        private List<LineItem> line_items;
        private String success_url;
        private String cancel_url;
        private String description;
    }

    @Data
    @Builder
    public static class LineItem {
        private Integer amount;
        private String currency;
        private String description;
        private String name;
        private Integer quantity;
    }
}
