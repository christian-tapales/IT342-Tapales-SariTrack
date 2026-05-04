package edu.cit.tapales.saritrack.repository;

import edu.cit.tapales.saritrack.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymongoId(String paymongoId);
}
