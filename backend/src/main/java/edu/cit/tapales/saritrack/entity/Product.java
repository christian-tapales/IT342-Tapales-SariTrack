package edu.cit.tapales.saritrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor; // Add this
import lombok.Builder;        // Add this
import lombok.Data;
import lombok.NoArgsConstructor;  // Add this

@Entity
@Table(name = "products")
@Data
@Builder           // Enables the Pattern
@AllArgsConstructor // Required for Builder
@NoArgsConstructor  // Required for JPA
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long vendorId;
    private String name;
    private String barcode;
    private Double price;
    private Integer stockQuantity; 
    private String imageUrl;
}