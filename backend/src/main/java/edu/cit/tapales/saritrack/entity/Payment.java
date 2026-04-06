package edu.cit.tapales.saritrack.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId; // FK to the Orders table

    private Double amount;
    
    private String paymentMethod; // e.g., "Cash", "GCash", "PayMongo"
    
    private String status; // e.g., "PENDING", "COMPLETED", "FAILED"

    private String paymongoId; // Placeholder for future API integration

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}