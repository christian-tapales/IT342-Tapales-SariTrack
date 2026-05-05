package edu.cit.tapales.saritrack.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;
    
    @Column(name = "vendor_id")
    private Long vendorId;
    
    private LocalDateTime timestamp;
    
    private String status = "PENDING"; // PENDING, PAID, CANCELLED, DEBT
    
    @Column(name = "customer_id")
    private Long customerId;

    // A transaction has many items (e.g., 2 Cokes, 1 Bread)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id")
    private List<OrderItem> items;
}