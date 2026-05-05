package edu.cit.tapales.saritrack.repository;

import edu.cit.tapales.saritrack.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByVendorIdOrderByTimestampDesc(Long vendorId);
    List<Notification> findByVendorIdAndIsReadOrderByTimestampDesc(Long vendorId, Boolean isRead);
}
