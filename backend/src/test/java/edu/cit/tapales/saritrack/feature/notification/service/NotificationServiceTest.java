package edu.cit.tapales.saritrack.feature.notification.service;

import edu.cit.tapales.saritrack.feature.notification.entity.Notification;
import edu.cit.tapales.saritrack.feature.notification.repository.NotificationRepository;
import edu.cit.tapales.saritrack.feature.product.entity.Product;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testSyncLowStockNotifications_TriggersWhenStockIsLow() {
        // Arrange
        Product lowStockProduct = new Product();
        lowStockProduct.setName("Oishi");
        lowStockProduct.setStockQuantity(2); // Low stock
        
        when(productRepository.findByVendorId(1L)).thenReturn(Arrays.asList(lowStockProduct));
        when(notificationRepository.findByVendorIdAndIsReadOrderByTimestampDesc(1L, false))
                .thenReturn(Collections.emptyList());

        // Act
        notificationService.syncLowStockNotifications(1L);

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testSyncLowStockNotifications_DoesNotSpamIfAlreadyNotified() {
        // Arrange
        Product lowStockProduct = new Product();
        lowStockProduct.setName("Oishi");
        lowStockProduct.setStockQuantity(2);
        
        Notification existing = new Notification();
        existing.setMessage("Product 'Oishi' was found low");
        
        when(productRepository.findByVendorId(1L)).thenReturn(Arrays.asList(lowStockProduct));
        when(notificationRepository.findByVendorIdAndIsReadOrderByTimestampDesc(1L, false))
                .thenReturn(Arrays.asList(existing));

        // Act
        notificationService.syncLowStockNotifications(1L);

        // Assert
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
