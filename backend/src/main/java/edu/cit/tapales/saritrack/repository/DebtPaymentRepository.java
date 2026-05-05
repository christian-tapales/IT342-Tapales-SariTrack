package edu.cit.tapales.saritrack.repository;

import edu.cit.tapales.saritrack.entity.DebtPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DebtPaymentRepository extends JpaRepository<DebtPayment, Long> {
    List<DebtPayment> findByCustomerId(Long customerId);
}
