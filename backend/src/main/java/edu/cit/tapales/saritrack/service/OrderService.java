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

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Order completeSale(Order transaction) {
        // 1. Apply Strategy Pattern for pricing
        double finalTotal = discountStrategy.apply(transaction.getTotalAmount());
        transaction.setTotalAmount(finalTotal);
        
        // 2. Set the transaction time
        transaction.setTimestamp(LocalDateTime.now());
        
        // 3. Handle Cash or Debt sales
        if ("PAID".equals(transaction.getStatus()) || "DEBT".equals(transaction.getStatus())) {
            deductStock(transaction);
            
            // If it's a debt, update the customer's balance
            if ("DEBT".equals(transaction.getStatus()) && transaction.getCustomerId() != null) {
                updateCustomerDebt(transaction.getCustomerId(), transaction.getTotalAmount());
            }
        }
        
        // 4. Save the finalized order
        return orderRepository.save(transaction);
    }

    @Transactional
    public void finalizeDigitalOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        if ("PAID".equals(order.getStatus())) {
            return; // Already processed
        }

        deductStock(order);
        order.setStatus("PAID");
        orderRepository.save(order);
    }

    private void deductStock(Order transaction) {
        for (OrderItem item : transaction.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            if (!product.getVendorId().equals(transaction.getVendorId())) {
                throw new RuntimeException("Security Error: Product " + product.getName() + " does not belong to this vendor!");
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }
    }

    private void updateCustomerDebt(Long customerId, Double amount) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        
        double currentDebt = customer.getCurrentDebt() != null ? customer.getCurrentDebt() : 0.0;
        customer.setCurrentDebt(currentDebt + amount);
        customer.setLastUpdate(LocalDateTime.now());
        customer.setStatus("Unpaid");
        customerRepository.save(customer);
        System.out.println("--- DEBT UPDATED FOR " + customer.getFullName() + ": +P" + amount + " ---");
    }
}