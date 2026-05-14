package edu.cit.tapales.saritrack.feature.order.service;

import edu.cit.tapales.saritrack.feature.customer.entity.Customer;
import edu.cit.tapales.saritrack.feature.customer.repository.CustomerRepository;
import edu.cit.tapales.saritrack.feature.notification.service.EmailService;
import edu.cit.tapales.saritrack.feature.notification.service.NotificationService;
import edu.cit.tapales.saritrack.feature.order.entity.Order;
import edu.cit.tapales.saritrack.feature.order.entity.OrderItem;
import edu.cit.tapales.saritrack.feature.order.repository.OrderRepository;
import edu.cit.tapales.saritrack.feature.product.entity.Product;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private DiscountStrategy discountStrategy;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Sardines");
        testProduct.setStockQuantity(10);
        testProduct.setPrice(25.0);

        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setPriceAtSale(25.0);

        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        testOrder = new Order();
        testOrder.setTotalAmount(50.0);
        testOrder.setStatus("PAID");
        testOrder.setItems(items);
        testOrder.setVendorId(1L);
    }

    @Test
    void testCompleteSale_DeductsStockAndSavesOrder() {
        when(discountStrategy.apply(anyDouble())).thenReturn(50.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order savedOrder = orderService.completeSale(testOrder);

        assertNotNull(savedOrder);
        assertEquals(8, testProduct.getStockQuantity());
        verify(productRepository).save(testProduct);
    }

    @Test
    void testCompleteSale_DebtRecordsCustomerBalance() {
        testOrder.setStatus("DEBT");
        testOrder.setCustomerId(99L);
        
        Customer customer = new Customer();
        customer.setId(99L);
        customer.setCurrentDebt(100.0);
        
        when(discountStrategy.apply(anyDouble())).thenReturn(50.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(customerRepository.findById(99L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        orderService.completeSale(testOrder);

        assertEquals(150.0, customer.getCurrentDebt());
        verify(customerRepository).save(customer);
    }

    @Test
    void testFinalizeDigitalOrder_DeductsStockAndUpdatesStatus() {
        testOrder.setId(500L);
        testOrder.setStatus("PENDING");
        
        when(orderRepository.findById(500L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        orderService.finalizeDigitalOrder(500L);

        assertEquals("PAID", testOrder.getStatus());
        assertEquals(8, testProduct.getStockQuantity());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void testFinalizeDigitalOrder_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.finalizeDigitalOrder(999L));
    }

    @Test
    void testFinalizeDigitalOrder_AlreadyPaid_DoesNothing() {
        Order order = new Order();
        order.setStatus("PAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.finalizeDigitalOrder(1L);

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCompleteSale_LowStockAlert_ShouldTriggerNotification() {
        testProduct.setStockQuantity(6);
        when(discountStrategy.apply(anyDouble())).thenReturn(50.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.completeSale(testOrder);

        verify(notificationService).createNotification(eq(1L), eq("Low Stock Alert!"), anyString(), eq("WARNING"));
    }

    @Test
    void testCompleteSale_HighDebtWarning_ShouldTriggerNotification() {
        testOrder.setStatus("DEBT");
        testOrder.setCustomerId(99L);
        testOrder.setTotalAmount(1100.0);
        
        Customer customer = new Customer();
        customer.setId(99L);
        customer.setVendorId(1L);
        customer.setCurrentDebt(0.0);
        
        when(discountStrategy.apply(anyDouble())).thenReturn(1100.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(customerRepository.findById(99L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.completeSale(testOrder);

        verify(notificationService).createNotification(eq(1L), eq("High Debt Warning"), anyString(), eq("WARNING"));
    }

    @Test
    void testCompleteSale_ProductNotFound_ThrowsException() {
        when(discountStrategy.apply(anyDouble())).thenReturn(100.0);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.completeSale(testOrder));
    }
}
