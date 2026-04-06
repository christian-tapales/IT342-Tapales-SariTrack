package edu.cit.tapales.saritrack.repository;
import edu.cit.tapales.saritrack.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}