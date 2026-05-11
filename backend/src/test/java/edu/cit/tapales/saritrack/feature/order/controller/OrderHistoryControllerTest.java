package edu.cit.tapales.saritrack.feature.order.controller;

import edu.cit.tapales.saritrack.feature.order.entity.Order;
import edu.cit.tapales.saritrack.feature.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepository;

    // Security mocks
    @MockBean
    private edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    private edu.cit.tapales.saritrack.feature.auth.repository.UserRepository userRepository;
    @MockBean
    private edu.cit.tapales.saritrack.core.security.JwtUtils jwtUtils;
    @MockBean
    private edu.cit.tapales.saritrack.core.security.JwtFilter jwtFilter;

    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        order1 = new Order();
        order1.setId(1L);
        order1.setVendorId(100L);
        order1.setTimestamp(LocalDateTime.now().minusDays(1));

        order2 = new Order();
        order2.setId(2L);
        order2.setVendorId(100L);
        order2.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testGetOrderHistory_VendorOnly_ShouldReturnSortedList() throws Exception {
        when(orderRepository.findByVendorId(100L)).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/api/orders/history?vendorId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L)) // order2 is newer
                .andExpect(jsonPath("$[1].id").value(1L));
    }

    @Test
    void testGetOrderHistory_WithCustomer_ShouldFilterByCustomer() throws Exception {
        order1.setCustomerId(50L);
        order2.setCustomerId(51L);
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/api/orders/history?vendorId=100&customerId=50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
