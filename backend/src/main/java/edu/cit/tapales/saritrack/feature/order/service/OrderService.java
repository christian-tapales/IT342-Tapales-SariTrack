package edu.cit.tapales.saritrack.feature.order.service;
import edu.cit.tapales.saritrack.feature.notification.service.NotificationService;
import edu.cit.tapales.saritrack.feature.customer.repository.CustomerRepository;
import edu.cit.tapales.saritrack.feature.customer.entity.Customer;
import edu.cit.tapales.saritrack.feature.order.repository.OrderRepository;
import edu.cit.tapales.saritrack.feature.order.entity.OrderItem;
import edu.cit.tapales.saritrack.feature.order.entity.Order;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import edu.cit.tapales.saritrack.feature.product.entity.Product;


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
    private edu.cit.tapales.saritrack.feature.notification.service.EmailService emailService;

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
        
        Order savedOrder = orderRepository.save(transaction);
        sendReceipt(savedOrder);
        return savedOrder;
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

        sendReceipt(order);

        notificationService.createNotification(
            order.getVendorId(), 
            "Digital Payment Success", 
            "Order #" + order.getId() + " has been paid successfully via Digital Wallet.", 
            "SUCCESS"
        );
    }

    private void sendReceipt(Order order) {
        if (order.getCustomerId() == null) return;
        
        customerRepository.findById(order.getCustomerId()).ifPresent(customer -> {
            if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                String subject = "Receipt for Order #" + order.getId() + " - SariTrack";
                
                // Format Date
                String formattedDate = order.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a"));

                // Build HTML Body
                StringBuilder html = new StringBuilder();
                html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; border-radius: 20px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);'>");
                
                // Header
                html.append("<div style='background-color: #16A394; color: white; padding: 30px; text-align: center;'>");
                html.append("<h1 style='margin: 0; font-size: 28px; font-weight: 900;'>Sari<span style='color: #0f172a; opacity: 0.8;'>Track</span></h1>");
                html.append("<p style='margin: 5px 0 0; opacity: 0.9; font-size: 14px;'>Digital Receipt</p>");
                html.append("</div>");
                
                // Body
                html.append("<div style='padding: 40px;'>");
                html.append("<h2 style='color: #333; margin-top: 0;'>Kumusta, ").append(customer.getFullName()).append("!</h2>");
                html.append("<p style='color: #666;'>Thank you for shopping at our store. Here is the summary of your transaction.</p>");
                
                // Order Info Table
                html.append("<div style='margin-top: 30px; padding: 20px; background-color: #f9f9f9; border-radius: 15px;'>");
                html.append("<table style='width: 100%; border-collapse: collapse;'>");
                html.append("<tr><td style='color: #999; font-size: 12px; text-transform: uppercase;'>Order ID</td><td style='text-align: right; font-weight: bold;'>#").append(order.getId()).append("</td></tr>");
                html.append("<tr><td style='color: #999; font-size: 12px; text-transform: uppercase; padding-top: 10px;'>Date</td><td style='text-align: right; font-weight: bold; padding-top: 10px;'>").append(formattedDate).append("</td></tr>");
                html.append("<tr><td style='color: #999; font-size: 12px; text-transform: uppercase; padding-top: 10px;'>Status</td><td style='text-align: right; font-weight: bold; color: ").append("DEBT".equals(order.getStatus()) ? "#f59e0b" : "#10b981").append("; padding-top: 10px;'>").append(order.getStatus()).append("</td></tr>");
                html.append("</table>");
                html.append("</div>");

                // Items Table
                html.append("<div style='margin-top: 30px;'>");
                html.append("<table style='width: 100%; border-collapse: collapse; font-size: 14px;'>");
                html.append("<tr style='border-bottom: 2px solid #f9f9f9;'><th style='text-align: left; padding: 10px 0; color: #999;'>Item</th><th style='text-align: center; padding: 10px 0; color: #999;'>Qty</th><th style='text-align: right; padding: 10px 0; color: #999;'>Price</th></tr>");
                
                if (order.getItems() != null) {
                    for (OrderItem item : order.getItems()) {
                        String pName = "Unknown Product";
                        if (item.getProduct() != null) {
                            pName = item.getProduct().getName();
                        } else if (item.getProductId() != null) {
                            pName = productRepository.findById(item.getProductId())
                                    .map(p -> p.getName())
                                    .orElse("Deleted Product");
                        }
                        
                        html.append("<tr style='border-bottom: 1px solid #f9f9f9;'>");
                        html.append("<td style='padding: 12px 0;'>").append(pName).append("</td>");
                        html.append("<td style='padding: 12px 0; text-align: center;'>").append(item.getQuantity()).append("</td>");
                        html.append("<td style='padding: 12px 0; text-align: right; font-weight: bold;'>₱").append(String.format("%.2f", item.getPriceAtSale() * item.getQuantity())).append("</td>");
                        html.append("</tr>");
                    }
                }
                html.append("</table>");
                html.append("</div>");

                // Total
                html.append("<div style='margin-top: 30px; border-top: 2px dashed #eee; padding-top: 20px; text-align: right;'>");
                html.append("<span style='color: #666; font-weight: bold;'>TOTAL AMOUNT</span>");
                html.append("<h1 style='color: #16A394; margin: 5px 0;'>₱").append(String.format("%.2f", order.getTotalAmount())).append("</h1>");
                html.append("</div>");
                
                html.append("<p style='margin-top: 40px; color: #999; font-size: 12px; text-align: center;'>See you again soon at SariTrack!</p>");
                html.append("</div>");
                html.append("</div>");

                emailService.sendEmail(customer.getEmail(), subject, html.toString());
            }
        });
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

    public java.util.List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}