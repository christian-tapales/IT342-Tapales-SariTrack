package edu.cit.tapales.saritrack.feature.order.controller;

import edu.cit.tapales.saritrack.feature.order.entity.Order;
import edu.cit.tapales.saritrack.feature.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        try {
            Order savedOrder = orderService.completeSale(order);
            return ResponseEntity.ok(savedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<java.util.List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        java.util.List<Order> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) Long vendorId,
            @RequestBody(required = false) java.util.Map<String, Object> payload) {
        Long targetVendorId = vendorId;
        String reason = null;
        if (payload != null) {
            if (targetVendorId == null && payload.get("vendorId") != null) {
                targetVendorId = Long.valueOf(payload.get("vendorId").toString());
            }
            if (payload.get("reason") != null) {
                reason = payload.get("reason").toString();
            }
        }
        if (targetVendorId == null) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "vendorId is required"));
        }
        Order cancelledOrder = orderService.cancelOrder(id, targetVendorId, reason);
        return ResponseEntity.ok(cancelledOrder);
    }
}