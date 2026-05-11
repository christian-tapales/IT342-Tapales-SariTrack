package edu.cit.tapales.saritrack.feature.product.repository;

import edu.cit.tapales.saritrack.feature.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByVendorId_ShouldReturnCorrectProducts() {
        // Arrange
        Product p1 = Product.builder().vendorId(1L).name("Coke").price(20.0).stockQuantity(10).build();
        Product p2 = Product.builder().vendorId(1L).name("Pepsi").price(20.0).stockQuantity(5).build();
        Product p3 = Product.builder().vendorId(2L).name("Sprite").price(20.0).stockQuantity(5).build();
        productRepository.saveAll(List.of(p1, p2, p3));

        // Act
        List<Product> vendor1Products = productRepository.findByVendorId(1L);

        // Assert
        assertEquals(2, vendor1Products.size());
        assertTrue(vendor1Products.stream().allMatch(p -> p.getVendorId().equals(1L)));
    }

    @Test
    void testFindByBarcodeAndVendorId_ShouldReturnMatch() {
        // Arrange
        Product p1 = Product.builder().vendorId(1L).name("Coke").barcode("123").price(20.0).stockQuantity(10).build();
        productRepository.save(p1);

        // Act
        Optional<Product> found = productRepository.findByBarcodeAndVendorId("123", 1L);
        Optional<Product> notFound = productRepository.findByBarcodeAndVendorId("999", 1L);
        Optional<Product> wrongVendor = productRepository.findByBarcodeAndVendorId("123", 2L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Coke", found.get().getName());
        assertFalse(notFound.isPresent());
        assertFalse(wrongVendor.isPresent());
    }

    @Test
    void testSumStockQuantity_ShouldReturnTotal() {
        // Arrange
        Product p1 = Product.builder().vendorId(1L).name("Coke").stockQuantity(10).build();
        Product p2 = Product.builder().vendorId(1L).name("Pepsi").stockQuantity(5).build();
        productRepository.saveAll(List.of(p1, p2));

        // Act
        Long totalStock = productRepository.sumStockQuantity();

        // Assert
        assertEquals(15L, totalStock);
    }
}
