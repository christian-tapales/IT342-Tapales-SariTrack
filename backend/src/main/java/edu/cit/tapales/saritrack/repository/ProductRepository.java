package edu.cit.tapales.saritrack.repository;
import edu.cit.tapales.saritrack.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByVendorId(Long vendorId);
    
    java.util.Optional<Product> findByBarcodeAndVendorId(String barcode, Long vendorId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.stockQuantity) FROM Product p")
    Long sumStockQuantity();
}