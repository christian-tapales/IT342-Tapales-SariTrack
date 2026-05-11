package edu.cit.tapales.saritrack.feature.notification.repository;

import edu.cit.tapales.saritrack.feature.notification.entity.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void testFindByVendorIdAndIsReadOrderByTimestampDesc() {
        // Arrange
        Notification n1 = new Notification();
        n1.setVendorId(1L);
        n1.setIsRead(false);
        n1.setTimestamp(LocalDateTime.now().minusDays(1));
        
        Notification n2 = new Notification();
        n2.setVendorId(1L);
        n2.setIsRead(false);
        n2.setTimestamp(LocalDateTime.now());
        
        Notification n3 = new Notification();
        n3.setVendorId(1L);
        n3.setIsRead(true);

        notificationRepository.saveAll(List.of(n1, n2, n3));

        // Act
        List<Notification> result = notificationRepository.findByVendorIdAndIsReadOrderByTimestampDesc(1L, false);

        // Assert
        assertEquals(2, result.size());
        assertEquals(n2.getTimestamp(), result.get(0).getTimestamp()); // Sorted by Desc
    }
}
