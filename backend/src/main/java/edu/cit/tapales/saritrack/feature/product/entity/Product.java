package edu.cit.tapales.saritrack.feature.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id")
    private Long vendorId;
    
    private String name;
    private String barcode;
    private Double price;
    private Integer stockQuantity; 
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
}