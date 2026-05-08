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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
        // Arrange
        when(discountStrategy.apply(anyDouble())).thenReturn(50.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Order savedOrder = orderService.completeSale(testOrder);

        // Assert
        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getTimestamp());
        assertEquals(50.0, savedOrder.getTotalAmount());
        
        // Verify stock was deducted (10 - 2 = 8)
        assertEquals(8, testProduct.getStockQuantity());
        verify(productRepository, times(1)).save(testProduct);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCompleteSale_DebtRecordsCustomerBalance() {
        // Arrange
        testOrder.setStatus("DEBT");
        testOrder.setCustomerId(99L);
        
        Customer customer = new Customer();
        customer.setId(99L);
        customer.setCurrentDebt(100.0);
        
        when(discountStrategy.apply(anyDouble())).thenReturn(50.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(customerRepository.findById(99L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        orderService.completeSale(testOrder);

        // Assert
        // Verify debt increased (100 + 50 = 150)
        assertEquals(150.0, customer.getCurrentDebt());
        verify(customerRepository, times(1)).save(customer);
        verify(notificationService, times(1)).createNotification(any(), anyString(), anyString(), anyString());
    }
}
