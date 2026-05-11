package edu.cit.tapales.saritrack.feature.payment.repository;

import edu.cit.tapales.saritrack.feature.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymongoId(String paymongoId);
}
