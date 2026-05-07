package edu.cit.tapales.saritrack.feature.customer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long vendorId;
    private String fullName;
    private String email;
    private Double currentDebt;
    private String status; // "Unpaid" or "Partial"
    private LocalDateTime lastUpdate;
}