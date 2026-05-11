package edu.cit.tapales.saritrack.feature.payment.repository;

import edu.cit.tapales.saritrack.feature.payment.entity.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void testFindByPaymongoId_ShouldReturnPayment() {
        Payment p = new Payment();
        p.setPaymongoId("cs_123");
        p.setOrderId(100L);
        p.setAmount(500.0);
        p.setStatus("PENDING");
        paymentRepository.save(p);

        Optional<Payment> found = paymentRepository.findByPaymongoId("cs_123");
        assertTrue(found.isPresent());
        assertEquals(100L, found.get().getOrderId());
    }
}
