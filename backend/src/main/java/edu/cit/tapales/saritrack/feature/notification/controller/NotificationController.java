package edu.cit.tapales.saritrack.feature.notification.controller;

import edu.cit.tapales.saritrack.feature.notification.entity.Notification;
import edu.cit.tapales.saritrack.feature.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> getNotifications(@RequestParam Long vendorId) {
        return notificationService.getVendorNotifications(vendorId);
    }

    @PostMapping("/sync")
    public void syncNotifications(@RequestParam Long vendorId) {
        notificationService.syncLowStockNotifications(vendorId);
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @PostMapping("/read-all")
    public void markAllAsRead(@RequestParam Long vendorId) {
        notificationService.markAllAsRead(vendorId);
    }
}
