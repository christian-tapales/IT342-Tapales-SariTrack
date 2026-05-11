package edu.cit.tapales.saritrack.feature.vendor.controller;

import edu.cit.tapales.saritrack.feature.customer.repository.CustomerRepository;
import edu.cit.tapales.saritrack.feature.order.repository.OrderRepository;
import edu.cit.tapales.saritrack.feature.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendorDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class VendorDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CustomerRepository customerRepository;

    // Security mocks
    @MockBean
    private edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    private edu.cit.tapales.saritrack.feature.auth.repository.UserRepository userRepository;
    @MockBean
    private edu.cit.tapales.saritrack.core.security.JwtUtils jwtUtils;
    @MockBean
    private edu.cit.tapales.saritrack.core.security.JwtFilter jwtFilter;

    @Test
    void testGetDashboardStats_ShouldReturnAggregatedData() throws Exception {
        // Arrange
        when(orderRepository.findByVendorIdAndStatusAndTimestampAfter(anyLong(), eq("PAID"), any())).thenReturn(Collections.emptyList());
        when(productRepository.findByVendorId(anyLong())).thenReturn(Collections.emptyList());
        when(customerRepository.findByVendorId(anyLong())).thenReturn(Collections.emptyList());
        when(orderRepository.findByVendorId(anyLong())).thenReturn(Collections.emptyList());
        when(orderRepository.findByVendorIdAndStatus(anyLong(), eq("PAID"))).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/vendor/dashboard/stats?vendorId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todaySales").value(0.0))
                .andExpect(jsonPath("$.lowStockCount").value(0))
                .andExpect(jsonPath("$.totalDebt").value(0.0));
    }
}
