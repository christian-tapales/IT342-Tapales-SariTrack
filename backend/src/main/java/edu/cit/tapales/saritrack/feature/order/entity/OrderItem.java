package edu.cit.tapales.saritrack.feature.order.entity;
import edu.cit.tapales.saritrack.feature.product.entity.Product;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    private Integer quantity;
    private Double priceAtSale;
}