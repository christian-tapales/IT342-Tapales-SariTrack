package edu.cit.tapales.saritrack.service;

import edu.cit.tapales.saritrack.entity.*;
import edu.cit.tapales.saritrack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private DiscountStrategy discountStrategy;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public String completeSale(Order transaction) {
        // 1. Apply Strategy Pattern for pricing
        double finalTotal = discountStrategy.apply(transaction.getTotalAmount());
        transaction.setTotalAmount(finalTotal);
        
        // 2. Set the transaction time
        transaction.setTimestamp(LocalDateTime.now());
        
        // 3. Process items and deduct stock
        for (OrderItem item : transaction.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            // SaaS Hardening: Verify product ownership
            if (!product.getVendorId().equals(transaction.getVendorId())) {
                throw new RuntimeException("Security Error: Product " + product.getName() + " does not belong to this vendor!");
            }

            // Atomic Hardening: Use Exception to trigger @Transactional rollback
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        // 4. Save the finalized order
        orderRepository.save(transaction);
        return "Sale completed and stock updated!";
    }
}