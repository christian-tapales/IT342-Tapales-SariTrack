package edu.cit.tapales.saritrack.feature.order.repository;

import edu.cit.tapales.saritrack.feature.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testFindByVendorId_ShouldReturnOrders() {
        Order o1 = new Order();
        o1.setVendorId(1L);
        o1.setStatus("PAID");
        
        Order o2 = new Order();
        o2.setVendorId(1L);
        o2.setStatus("DEBT");
        
        orderRepository.saveAll(List.of(o1, o2));

        assertEquals(2, orderRepository.findByVendorId(1L).size());
    }

    @Test
    void testFindByCustomerId_ShouldReturnOrders() {
        Order o1 = new Order();
        o1.setVendorId(1L);
        o1.setCustomerId(99L);
        
        orderRepository.save(o1);

        assertEquals(1, orderRepository.findByCustomerId(99L).size());
    }

    @Test
    void testFindByVendorIdAndStatusAndTimestampAfter_ShouldReturnFilteredOrders() {
        Order o1 = new Order();
        o1.setVendorId(1L);
        o1.setStatus("PAID");
        o1.setTimestamp(LocalDateTime.now().minusHours(1));
        
        orderRepository.save(o1);

        LocalDateTime startOfDay = LocalDateTime.now().minusHours(2);
        List<Order> result = orderRepository.findByVendorIdAndStatusAndTimestampAfter(1L, "PAID", startOfDay);
        
        assertEquals(1, result.size());
    }
}
