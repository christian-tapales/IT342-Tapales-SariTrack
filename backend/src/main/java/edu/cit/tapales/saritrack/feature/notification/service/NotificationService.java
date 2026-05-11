package edu.cit.tapales.saritrack.feature.notification.service;

import edu.cit.tapales.saritrack.feature.notification.entity.Notification;
import edu.cit.tapales.saritrack.feature.product.entity.Product;
import edu.cit.tapales.saritrack.feature.notification.repository.NotificationRepository;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ProductRepository productRepository;

    public void createNotification(Long vendorId, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setVendorId(vendorId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setTimestamp(LocalDateTime.now());
        notification.setIsRead(false);
        notificationRepository.save(notification);
        System.out.println("--- NOTIFICATION CREATED: " + title + " ---");
    }

    public void syncLowStockNotifications(Long vendorId) {
        List<Product> lowStockProducts = productRepository.findByVendorId(vendorId).stream()
                .filter(p -> p.getStockQuantity() < 5)
                .toList();

        List<Notification> existingUnread = notificationRepository.findByVendorIdAndIsReadOrderByTimestampDesc(vendorId, false);

        for (Product product : lowStockProducts) {
            // Check if we already warned them about this specific product (to avoid spam)
            boolean alreadyNotified = existingUnread.stream()
                    .anyMatch(n -> n.getMessage().contains("'" + product.getName() + "'"));

            if (!alreadyNotified) {
                createNotification(
                    vendorId, 
                    "Low Stock Alert!", 
                    "Product '" + product.getName() + "' was found low on stock (" + product.getStockQuantity() + " left).", 
                    "WARNING"
                );
            }
        }
    }

    public List<Notification> getVendorNotifications(Long vendorId) {
        return notificationRepository.findByVendorIdOrderByTimestampDesc(vendorId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead(Long vendorId) {
        List<Notification> unread = notificationRepository.findByVendorIdAndIsReadOrderByTimestampDesc(vendorId, false);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }
}
