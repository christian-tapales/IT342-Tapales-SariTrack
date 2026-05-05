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

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Order completeSale(Order transaction) {
        double finalTotal = discountStrategy.apply(transaction.getTotalAmount());
        transaction.setTotalAmount(finalTotal);
        transaction.setTimestamp(LocalDateTime.now());
        
        if ("PAID".equals(transaction.getStatus()) || "DEBT".equals(transaction.getStatus())) {
            deductStock(transaction);
            
            if ("DEBT".equals(transaction.getStatus()) && transaction.getCustomerId() != null) {
                updateCustomerDebt(transaction.getCustomerId(), transaction.getTotalAmount());
                notificationService.createNotification(
                    transaction.getVendorId(), 
                    "New Utang Recorded", 
                    "A debt of ₱" + transaction.getTotalAmount() + " was added to a customer account.", 
                    "INFO"
                );
            }
        }
        
        return orderRepository.save(transaction);
    }

    @Transactional
    public void finalizeDigitalOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        if ("PAID".equals(order.getStatus())) {
            return;
        }

        deductStock(order);
        order.setStatus("PAID");
        orderRepository.save(order);

        notificationService.createNotification(
            order.getVendorId(), 
            "Digital Payment Success", 
            "Order #" + order.getId() + " has been paid successfully via Digital Wallet.", 
            "SUCCESS"
        );
    }

    private void deductStock(Order transaction) {
        for (OrderItem item : transaction.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            // Trigger Low Stock Notification
            if (product.getStockQuantity() < 5) {
                notificationService.createNotification(
                    transaction.getVendorId(), 
                    "Low Stock Alert!", 
                    "Product '" + product.getName() + "' is running low (" + product.getStockQuantity() + " left).", 
                    "WARNING"
                );
            }
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

        // Trigger High Debt Warning
        if (customer.getCurrentDebt() > 1000) {
            notificationService.createNotification(
                customer.getVendorId(), 
                "High Debt Warning", 
                customer.getFullName() + "'s debt has exceeded ₱1,000!", 
                "WARNING"
            );
        }
    }
}