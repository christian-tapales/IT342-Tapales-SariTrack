package edu.cit.tapales.saritrack.feature.order.repository;
import edu.cit.tapales.saritrack.feature.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByVendorId(Long vendorId);
    List<Order> findByVendorIdAndTimestampAfter(Long vendorId, LocalDateTime timestamp);
    List<Order> findByVendorIdAndStatus(Long vendorId, String status);
    List<Order> findByVendorIdAndStatusAndTimestampAfter(Long vendorId, String status, LocalDateTime timestamp);
    List<Order> findByCustomerId(Long customerId);
    
    long countByVendorId(Long vendorId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.vendorId = :vendorId AND (o.status = 'PAID' OR o.status = 'DEBT')")
    Double sumTotalAmountByVendorId(Long vendorId);
}