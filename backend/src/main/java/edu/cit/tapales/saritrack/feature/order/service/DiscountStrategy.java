package edu.cit.tapales.saritrack.feature.order.service;

public interface DiscountStrategy {
    double apply(double total);
}