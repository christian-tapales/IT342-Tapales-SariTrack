package edu.cit.tapales.saritrack.entity;

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

    private String name;
    private Double totalDebt;
    private String status; // "Unpaid" or "Partial"
    private LocalDateTime lastUpdate;
}