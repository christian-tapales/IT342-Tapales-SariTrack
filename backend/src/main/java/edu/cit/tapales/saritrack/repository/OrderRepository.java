package edu.cit.tapales.saritrack.repository;
import edu.cit.tapales.saritrack.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByVendorId(Long vendorId);
    List<Order> findByVendorIdAndTimestampAfter(Long vendorId, LocalDateTime timestamp);
}