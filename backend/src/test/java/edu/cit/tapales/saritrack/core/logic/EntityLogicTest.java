package edu.cit.tapales.saritrack.core.logic;

import edu.cit.tapales.saritrack.feature.product.entity.Product;
import edu.cit.tapales.saritrack.feature.order.entity.Order;
import edu.cit.tapales.saritrack.feature.order.entity.OrderItem;
import edu.cit.tapales.saritrack.feature.customer.entity.Customer;
import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.notification.entity.Notification;
import edu.cit.tapales.saritrack.feature.payment.entity.DebtPayment;
import edu.cit.tapales.saritrack.feature.admin.dto.PlatformStatsDTO;
import edu.cit.tapales.saritrack.feature.admin.dto.VendorAnalyticsDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityLogicTest {

    @Test
    void testProductBuilderAndGetters() {
        Product p = Product.builder()
                .id(1L)
                .name("Coke")
                .price(25.0)
                .stockQuantity(10)
                .vendorId(100L)
                .category("Drinks")
                .imageUrl("http://image.com")
                .barcode("123")
                .build();

        assertEquals(1L, p.getId());
        assertEquals("Coke", p.getName());
        assertEquals(25.0, p.getPrice());
        assertEquals(10, p.getStockQuantity());
        assertEquals(100L, p.getVendorId());
        assertEquals("Drinks", p.getCategory());
        assertEquals("http://image.com", p.getImageUrl());
        assertEquals("123", p.getBarcode());
    }

    @Test
    void testOrderEntity() {
        Order o = new Order();
        o.setId(1L);
        o.setTotalAmount(500.0);
        o.setStatus("PAID");
        o.setVendorId(100L);
        o.setCustomerId(99L);
        
        assertEquals(1L, o.getId());
        assertEquals(500.0, o.getTotalAmount());
        assertEquals("PAID", o.getStatus());
        assertEquals(100L, o.getVendorId());
        assertEquals(99L, o.getCustomerId());
    }

    @Test
    void testOrderItemEntity() {
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setProductId(10L);
        item.setQuantity(2);
        item.setPriceAtSale(50.0);
        
        assertEquals(1L, item.getId());
        assertEquals(10L, item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(50.0, item.getPriceAtSale());
    }

    @Test
    void testCustomerEntity() {
        Customer c = new Customer();
        c.setId(1L);
        c.setFullName("Juan Dela Cruz");
        c.setEmail("juan@gmail.com");
        c.setCurrentDebt(150.0);
        c.setVendorId(100L);
        
        assertEquals(1L, c.getId());
        assertEquals("Juan Dela Cruz", c.getFullName());
        assertEquals("juan@gmail.com", c.getEmail());
        assertEquals(150.0, c.getCurrentDebt());
        assertEquals(100L, c.getVendorId());
    }

    @Test
    void testUserEntity() {
        User u = new User();
        u.setId(1L);
        u.setEmail("test@gmail.com");
        u.setName("Test User");
        u.setRole("VENDOR");
        
        assertEquals(1L, u.getId());
        assertEquals("test@gmail.com", u.getEmail());
        assertEquals("Test User", u.getName());
        assertEquals("VENDOR", u.getRole());
    }

    @Test
    void testNotificationEntity() {
        Notification n = new Notification();
        n.setId(1L);
        n.setVendorId(100L);
        n.setTitle("Alert");
        n.setMessage("Low stock");
        n.setIsRead(true);
        
        assertEquals(1L, n.getId());
        assertEquals(100L, n.getVendorId());
        assertEquals("Alert", n.getTitle());
        assertTrue(n.getIsRead());
    }

    @Test
    void testPaymentEntity() {
        edu.cit.tapales.saritrack.feature.payment.entity.Payment p = new edu.cit.tapales.saritrack.feature.payment.entity.Payment();
        p.setId(1L);
        p.setOrderId(500L);
        p.setStatus("PAID");
        
        assertEquals(1L, p.getId());
        assertEquals(500L, p.getOrderId());
        assertEquals("PAID", p.getStatus());
    }

    @Test
    void testDebtPaymentEntity() {
        DebtPayment dp = new DebtPayment();
        dp.setId(1L);
        dp.setCustomerId(99L);
        dp.setAmount(150.0);
        
        assertEquals(1L, dp.getId());
        assertEquals(99L, dp.getCustomerId());
        assertEquals(150.0, dp.getAmount());
    }

    @Test
    void testPlatformStatsDTO() {
        PlatformStatsDTO stats = new PlatformStatsDTO();
        stats.setTotalPlatformSales(5000.0);
        stats.setTotalVendors(50);
        assertEquals(5000.0, stats.getTotalPlatformSales());
        assertEquals(50, stats.getTotalVendors());
    }

    @Test
    void testVendorAnalyticsDTO() {
        VendorAnalyticsDTO dto = new VendorAnalyticsDTO();
        dto.setName("Sari Store");
        dto.setTotalSales(2000.0);
        dto.setStatus("Active");
        assertEquals("Sari Store", dto.getName());
        assertEquals(2000.0, dto.getTotalSales());
        assertEquals("Active", dto.getStatus());
    }
}
