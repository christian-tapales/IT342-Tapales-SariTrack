package edu.cit.tapales.saritrack.feature.admin.service;

import edu.cit.tapales.saritrack.feature.admin.dto.PlatformStatsDTO;
import edu.cit.tapales.saritrack.feature.admin.dto.VendorAnalyticsDTO;
import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import edu.cit.tapales.saritrack.feature.order.entity.Order;
import edu.cit.tapales.saritrack.feature.order.repository.OrderRepository;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetPlatformStats_AggregatesCorrectly() {
        // Arrange
        Order order1 = new Order();
        order1.setTotalAmount(150.0);
        order1.setStatus("PAID");
        order1.setTimestamp(LocalDateTime.now());

        Order order2 = new Order();
        order2.setTotalAmount(50.0);
        order2.setStatus("DEBT");
        order2.setTimestamp(LocalDateTime.now());

        Order order3 = new Order();
        order3.setTotalAmount(300.0);
        order3.setStatus("CANCELLED"); // Should be ignored in totals
        order3.setTimestamp(LocalDateTime.now());

        User vendor1 = new User();
        vendor1.setId(1L);
        vendor1.setRole("VENDOR");

        User adminUser = new User();
        adminUser.setRole("ADMIN"); // Should be ignored in vendor count

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2, order3));
        when(userRepository.findAll()).thenReturn(Arrays.asList(vendor1, adminUser));
        when(productRepository.count()).thenReturn(15L);
        when(productRepository.sumStockQuantity()).thenReturn(500L);

        // Act
        PlatformStatsDTO stats = adminService.getPlatformStats();

        // Assert
        assertNotNull(stats);
        assertEquals(200.0, stats.getTotalPlatformSales()); // 150 + 50
        assertEquals(1, stats.getTotalVendors());
        assertEquals(15L, stats.getTotalSKUs());
        assertEquals(500L, stats.getTotalStock());
    }

    @Test
    void testGetAllVendorAnalytics_CalculatesStatusCorrectly() {
        // Arrange
        User vendor1 = new User();
        vendor1.setId(1L);
        vendor1.setName("Top Vendor");
        vendor1.setRole("VENDOR");

        User vendor2 = new User();
        vendor2.setId(2L);
        vendor2.setName("New Vendor");
        vendor2.setRole("VENDOR");

        when(userRepository.findAll()).thenReturn(Arrays.asList(vendor1, vendor2));
        
        when(orderRepository.sumTotalAmountByVendorId(1L)).thenReturn(6000.0);
        when(orderRepository.countByVendorId(1L)).thenReturn(50L);

        when(orderRepository.sumTotalAmountByVendorId(2L)).thenReturn(0.0);
        when(orderRepository.countByVendorId(2L)).thenReturn(0L);

        // Act
        List<VendorAnalyticsDTO> analyticsList = adminService.getAllVendorAnalytics();

        // Assert
        assertEquals(2, analyticsList.size());
        
        VendorAnalyticsDTO dto1 = analyticsList.stream().filter(a -> a.getId() == 1L).findFirst().get();
        assertEquals("Top Seller", dto1.getStatus()); // > 5000 sales
        
        VendorAnalyticsDTO dto2 = analyticsList.stream().filter(a -> a.getId() == 2L).findFirst().get();
        assertEquals("New Member", dto2.getStatus()); // 0 sales
    }
}
