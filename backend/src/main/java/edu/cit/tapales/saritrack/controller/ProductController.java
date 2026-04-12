package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.Product;
import edu.cit.tapales.saritrack.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Connects to your React app
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product save(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "Product deleted successfully";
    }
}