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
    public List<Order> getOrderHistory(
            @RequestParam Long vendorId, 
            @RequestParam(required = false) Long customerId) {
        
        List<Order> orders;
        if (customerId != null) {
            System.out.println("--- FETCHING DEBT HISTORY FOR CUSTOMER: " + customerId + " ---");
            orders = orderRepository.findAll().stream()
                    .filter(o -> customerId.equals(o.getCustomerId()))
                    .collect(Collectors.toList());
        } else {
            orders = orderRepository.findByVendorId(vendorId);
        }

        return orders.stream()
                .sorted(Comparator.comparing(Order::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
