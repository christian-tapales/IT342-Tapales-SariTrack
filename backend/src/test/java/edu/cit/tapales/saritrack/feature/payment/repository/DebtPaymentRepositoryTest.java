package edu.cit.tapales.saritrack.feature.payment.repository;

import edu.cit.tapales.saritrack.feature.payment.entity.DebtPayment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class DebtPaymentRepositoryTest {

    @Autowired
    private DebtPaymentRepository debtPaymentRepository;

    @Test
    void testFindByCustomerId_ShouldReturnPayments() {
        DebtPayment p1 = new DebtPayment();
        p1.setCustomerId(10L);
        p1.setAmount(100.0);
        
        DebtPayment p2 = new DebtPayment();
        p2.setCustomerId(10L);
        p2.setAmount(50.0);
        
        debtPaymentRepository.saveAll(List.of(p1, p2));

        List<DebtPayment> result = debtPaymentRepository.findByCustomerId(10L);
        assertEquals(2, result.size());
    }
}
