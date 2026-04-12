package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.Order;
import edu.cit.tapales.saritrack.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public String placeOrder(@RequestBody Order order) {
        // This calls your OrderService to deduct stock and save the sale
        return orderService.completeSale(order);
    }
}