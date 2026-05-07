package edu.cit.tapales.saritrack.feature.admin.service;
import edu.cit.tapales.saritrack.feature.admin.dto.PlatformStatsDTO;
import edu.cit.tapales.saritrack.feature.order.entity.Order;

import edu.cit.tapales.saritrack.feature.admin.dto.VendorAnalyticsDTO;
import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.order.repository.OrderRepository;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public PlatformStatsDTO getPlatformStats() {
        PlatformStatsDTO stats = new PlatformStatsDTO();
        
        // 1. Total Platform Sales
        Double totalSales = orderRepository.findAll().stream()
                .filter(o -> "PAID".equals(o.getStatus()) || "DEBT".equals(o.getStatus()))
                .mapToDouble(o -> o.getTotalAmount())
                .sum();
        stats.setTotalPlatformSales(totalSales);

        // 2. Total Vendors
        long vendorCount = userRepository.findAll().stream()
                .filter(u -> "VENDOR".equals(u.getRole()))
                .count();
        stats.setTotalVendors((int) vendorCount);

        // 3. Total SKUs and Stock
        stats.setTotalSKUs(productRepository.count());
        Long totalStock = productRepository.sumStockQuantity();
        stats.setTotalStock(totalStock != null ? totalStock : 0L);
        
        stats.setSystemHealth(99.9);

        // 4. Weekly Platform Revenue (Last 7 Days)
        java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0);
        List<Order> recentOrders = orderRepository.findAll().stream()
                .filter(o -> o.getTimestamp().isAfter(sevenDaysAgo))
                .filter(o -> "PAID".equals(o.getStatus()) || "DEBT".equals(o.getStatus()))
                .collect(Collectors.toList());

        java.util.List<java.util.Map<String, Object>> weeklySales = new java.util.ArrayList<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("EEE");
        
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            double dayTotal = recentOrders.stream()
                    .filter(o -> o.getTimestamp().toLocalDate().equals(date))
                    .mapToDouble(o -> o.getTotalAmount())
                    .sum();
            
            java.util.Map<String, Object> dayData = new java.util.HashMap<>();
            dayData.put("day", date.format(formatter));
            dayData.put("sales", dayTotal);
            weeklySales.add(dayData);
        }
        stats.setWeeklySales(weeklySales);

        // 5. Top Vendors by Sales
        List<VendorAnalyticsDTO> allVendorAnalytics = getAllVendorAnalytics();
        java.util.List<java.util.Map<String, Object>> topVendors = allVendorAnalytics.stream()
                .sorted((v1, v2) -> v2.getTotalSales().compareTo(v1.getTotalSales()))
                .limit(5)
                .map(v -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("name", v.getName());
                    map.put("sales", v.getTotalSales());
                    return map;
                })
                .collect(Collectors.toList());
        stats.setTopVendors(topVendors);

        // 6. Global Recent Transactions (Last 10)
        List<java.util.Map<String, Object>> globalTxns = orderRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
                .limit(10)
                .map(o -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", o.getId());
                    map.put("totalAmount", o.getTotalAmount());
                    map.put("status", o.getStatus());
                    map.put("timestamp", o.getTimestamp());
                    map.put("vendorId", o.getVendorId());
                    return map;
                })
                .collect(Collectors.toList());
        stats.setRecentTransactions(globalTxns);

        return stats;
    }

    public List<VendorAnalyticsDTO> getAllVendorAnalytics() {
        List<User> vendors = userRepository.findAll().stream()
                .filter(u -> "VENDOR".equals(u.getRole()))
                .collect(Collectors.toList());

        return vendors.stream().map(vendor -> {
            VendorAnalyticsDTO dto = new VendorAnalyticsDTO();
            dto.setId(vendor.getId());
            dto.setName(vendor.getName());
            dto.setEmail(vendor.getEmail());
            // Fallback for old users without createdAt
            dto.setRegistrationDate(vendor.getCreatedAt() != null ? vendor.getCreatedAt() : java.time.LocalDateTime.now());

            Double totalSales = orderRepository.sumTotalAmountByVendorId(vendor.getId());
            dto.setTotalSales(totalSales != null ? totalSales : 0.0);
            
            dto.setOrderCount((int) orderRepository.countByVendorId(vendor.getId()));

            // Determine status based on performance
            if (dto.getTotalSales() > 5000) {
                dto.setStatus("Top Seller");
            } else if (dto.getOrderCount() > 0) {
                dto.setStatus("Active");
            } else {
                dto.setStatus("New Member");
            }

            return dto;
        }).collect(Collectors.toList());
    }
}
