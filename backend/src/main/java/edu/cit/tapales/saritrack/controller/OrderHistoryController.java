package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.Order;
import edu.cit.tapales.saritrack.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderHistoryController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/history")
    public List<Order> getOrderHistory(@RequestParam Long vendorId) {
        System.out.println("--- FETCHING FULL ORDER HISTORY FOR VENDOR: " + vendorId + " ---");
        return orderRepository.findByVendorId(vendorId).stream()
                .sorted(Comparator.comparing(Order::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
