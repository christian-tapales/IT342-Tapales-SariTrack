package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.Product;
import edu.cit.tapales.saritrack.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Connects to your React app
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private edu.cit.tapales.saritrack.service.ProductLookupService productLookupService;

    @GetMapping("/lookup/{barcode}")
    public ResponseEntity<?> lookupByBarcode(@PathVariable String barcode) {
        String productName = productLookupService.lookupProductName(barcode);
        return ResponseEntity.ok(java.util.Map.of("productName", productName));
    }

    @GetMapping
    public List<Product> getAll(@RequestParam(required = false) Long vendorId) {
        if (vendorId != null) {
            return productRepository.findByVendorId(vendorId);
        }
        return productRepository.findAll();
    }

    @PostMapping
    public Product save(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails, @RequestParam Long vendorId) {
        System.out.println("--- UPDATE PRODUCT REQUEST RECEIVED FOR ID: " + id + " VENDOR: " + vendorId + " ---");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (!product.getVendorId().equals(vendorId)) {
            return ResponseEntity.status(403).body("Security Error: You do not own this product.");
        }

        product.setName(productDetails.getName());
        product.setBarcode(productDetails.getBarcode());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());
        
        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, @RequestParam Long vendorId) {
        Product product = productRepository.findById(id).orElse(null);
        
        if (product != null && product.getVendorId().equals(vendorId)) {
            productRepository.deleteById(id);
            return "Product deleted successfully";
        }
        
        return "Error: Unauthorized or product not found";
    }
}