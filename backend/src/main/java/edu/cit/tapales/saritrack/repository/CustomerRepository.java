package edu.cit.tapales.saritrack.repository;

import edu.cit.tapales.saritrack.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByVendorId(Long vendorId);
}