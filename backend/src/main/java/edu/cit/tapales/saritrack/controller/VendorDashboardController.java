package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.*;
import edu.cit.tapales.saritrack.repository.*;
// ... existing imports ...
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendor/dashboard")
public class VendorDashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats(@RequestParam Long vendorId) {
        System.out.println("--- DASHBOARD STATS REQUESTED FOR VENDOR: " + vendorId + " ---");
        Map<String, Object> stats = new HashMap<>();

        // 1. Calculate Today's Sales
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        List<Order> todaysOrders = orderRepository.findByVendorIdAndTimestampAfter(vendorId, startOfDay);
        double totalSales = todaysOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        stats.put("todaySales", totalSales);

        // 2. Count Low Stock Items (< 5)
        List<Product> allProducts = productRepository.findByVendorId(vendorId);
        long lowStockCount = allProducts.stream()
                .filter(p -> p.getStockQuantity() < 5)
                .count();
        stats.put("lowStockCount", lowStockCount);

        // 3. Calculate Total Listahan (Outstanding Credit)
        List<Customer> customers = customerRepository.findByVendorId(vendorId);
        double totalDebt = customers.stream()
                .filter(c -> c.getCurrentDebt() != null)
                .mapToDouble(Customer::getCurrentDebt)
                .sum();
        stats.put("totalDebt", totalDebt);

        // 4. Get Recent Transactions (Latest 5)
        List<Order> allOrders = orderRepository.findByVendorId(vendorId);
        List<Order> recentOrders = allOrders.stream()
                .sorted(Comparator.comparing(Order::getTimestamp).reversed())
                .limit(5)
                .collect(Collectors.toList());
        stats.put("recentTransactions", recentOrders);

        // 5. Calculate Top Selling Products
        Map<Long, Integer> productSales = new HashMap<>();
        for (Order order : allOrders) {
            for (OrderItem item : order.getItems()) {
                productSales.put(item.getProductId(), 
                    productSales.getOrDefault(item.getProductId(), 0) + item.getQuantity());
            }
        }

        List<Map<String, Object>> topSelling = productSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .map(entry -> {
                    Map<String, Object> m = new HashMap<>();
                    String name = productRepository.findById(entry.getKey())
                        .map(Product::getName)
                        .orElse("Unknown Product");
                    m.put("name", name);
                    m.put("sold", entry.getValue());
                    return m;
                })
                .collect(Collectors.toList());
        stats.put("topSelling", topSelling);

        return stats;
    }
}
