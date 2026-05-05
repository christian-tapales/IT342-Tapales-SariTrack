package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.*;
import edu.cit.tapales.saritrack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
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

        // 1. Calculate Today's Sales (PAID ONLY)
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        List<Order> todaysPaidOrders = orderRepository.findByVendorIdAndStatusAndTimestampAfter(vendorId, "PAID", startOfDay);
        double totalSales = todaysPaidOrders.stream()
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

        // 4. Get Recent Transactions (Latest 5, includes Pending to show activity)
        List<Order> allOrders = orderRepository.findByVendorId(vendorId);
        List<Order> recentOrders = allOrders.stream()
                .sorted(Comparator.comparing(Order::getTimestamp).reversed())
                .limit(5)
                .collect(Collectors.toList());
        stats.put("recentTransactions", recentOrders);

        // 5. Calculate Top Selling Products (PAID ONLY)
        List<Order> allPaidOrders = orderRepository.findByVendorIdAndStatus(vendorId, "PAID");
        Map<Long, Integer> productSales = new HashMap<>();
        for (Order order : allPaidOrders) {
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

        // 6. Calculate Weekly Sales Trend (Last 7 Days)
        List<Map<String, Object>> weeklySales = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = LocalDateTime.now().minusDays(i).with(LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.now().minusDays(i).with(LocalTime.MAX);
            
            double dayTotal = allPaidOrders.stream()
                    .filter(o -> o.getTimestamp().isAfter(dayStart) && o.getTimestamp().isBefore(dayEnd))
                    .mapToDouble(Order::getTotalAmount)
                    .sum();
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", dayStart.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            dayData.put("sales", dayTotal);
            weeklySales.add(dayData);
        }
        stats.put("weeklySales", weeklySales);

        return stats;
    }
}
