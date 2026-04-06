package edu.cit.tapales.saritrack.repository;
import edu.cit.tapales.saritrack.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {}