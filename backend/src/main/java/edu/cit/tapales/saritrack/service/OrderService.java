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
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public String completeSale(Order transaction) {
        transaction.setTimestamp(LocalDateTime.now());

        for (OrderItem item : transaction.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < item.getQuantity()) {
                return "Error: Insufficient stock for " + product.getName();
            }

            // Deduct stock!
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.save(transaction);
        return "Sale completed and stock updated!";
    }
}