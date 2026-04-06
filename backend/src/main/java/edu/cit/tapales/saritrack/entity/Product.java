package edu.cit.tapales.saritrack.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data // This is what creates the getStockQuantity() method!
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