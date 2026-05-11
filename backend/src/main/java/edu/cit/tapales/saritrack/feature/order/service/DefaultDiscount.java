package edu.cit.tapales.saritrack.feature.order.service;
import org.springframework.stereotype.Component;

@Component
public class DefaultDiscount implements DiscountStrategy {
    @Override
    public double apply(double total) {
        return total; // No discount logic for now
    }
}