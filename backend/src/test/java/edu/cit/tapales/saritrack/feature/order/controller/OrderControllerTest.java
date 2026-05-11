package edu.cit.tapales.saritrack.feature.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.tapales.saritrack.feature.order.entity.Order;
import edu.cit.tapales.saritrack.feature.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    // Security mocks for ApplicationContext
    @MockBean
    private edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    private edu.cit.tapales.saritrack.feature.auth.repository.UserRepository userRepository;
    @MockBean
    private edu.cit.tapales.saritrack.core.security.JwtUtils jwtUtils;
    @MockBean
    private edu.cit.tapales.saritrack.core.security.JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setTotalAmount(100.0);
        testOrder.setStatus("PAID");
    }

    @Test
    void testPlaceOrder_Success_ShouldReturnOrder() throws Exception {
        when(orderService.completeSale(any(Order.class))).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void testPlaceOrder_Error_ShouldReturnBadRequest() throws Exception {
        when(orderService.completeSale(any(Order.class))).thenThrow(new RuntimeException("Out of stock"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Out of stock"));
    }

    @Test
    void testGetOrdersByCustomer_ShouldReturnList() throws Exception {
        when(orderService.getOrdersByCustomer(99L)).thenReturn(List.of(testOrder));

        mockMvc.perform(get("/api/orders/customer/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
