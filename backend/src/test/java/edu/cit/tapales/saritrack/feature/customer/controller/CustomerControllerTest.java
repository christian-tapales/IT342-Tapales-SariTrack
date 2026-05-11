package edu.cit.tapales.saritrack.feature.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.tapales.saritrack.feature.customer.entity.Customer;
import edu.cit.tapales.saritrack.feature.customer.repository.CustomerRepository;
import edu.cit.tapales.saritrack.feature.notification.service.NotificationService;
import edu.cit.tapales.saritrack.feature.payment.repository.DebtPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private DebtPaymentRepository debtPaymentRepository;

    @MockBean
    private NotificationService notificationService;

    // Security mocks
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

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFullName("Juan Dela Cruz");
        testCustomer.setCurrentDebt(500.0);
        testCustomer.setVendorId(100L);
    }

    @Test
    void testGetCustomers_ShouldReturnList() throws Exception {
        when(customerRepository.findByVendorId(100L)).thenReturn(List.of(testCustomer));

        mockMvc.perform(get("/api/customers?vendorId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Juan Dela Cruz"));
    }

    @Test
    void testAddCustomer_ShouldReturnSavedCustomer() throws Exception {
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Juan Dela Cruz"));
    }

    @Test
    void testRecordPayment_ShouldDeductDebtAndNotify() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        mockMvc.perform(post("/api/customers/1/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 200.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentDebt").value(300.0));

        verify(debtPaymentRepository, times(1)).save(any());
        verify(notificationService, times(1)).createNotification(anyLong(), eq("Payment Received!"), anyString(), eq("SUCCESS"));
    }

    @Test
    void testDeleteCustomer_ShouldCallDelete() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isOk());

        verify(customerRepository, times(1)).deleteById(1L);
    }
}
