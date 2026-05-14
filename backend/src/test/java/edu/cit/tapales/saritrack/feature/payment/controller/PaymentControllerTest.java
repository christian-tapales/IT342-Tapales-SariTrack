package edu.cit.tapales.saritrack.feature.payment.controller;

import edu.cit.tapales.saritrack.feature.payment.repository.PaymentRepository;
import edu.cit.tapales.saritrack.feature.payment.service.PayMongoService;
import edu.cit.tapales.saritrack.feature.payment.dto.CheckoutSessionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;



import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayMongoService payMongoService;

    @MockitoBean
    private PaymentRepository paymentRepository;

    // Security mocks
    @MockitoBean
    private edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean
    private edu.cit.tapales.saritrack.feature.auth.repository.UserRepository userRepository;
    @MockitoBean
    private edu.cit.tapales.saritrack.core.security.JwtUtils jwtUtils;
    @MockitoBean
    private edu.cit.tapales.saritrack.core.security.JwtFilter jwtFilter;

    @Test
    void testCreateSession_Success_ShouldReturnUrl() throws Exception {
        // Arrange
        CheckoutSessionResponse mockResponse = new CheckoutSessionResponse();
        CheckoutSessionResponse.DataContainer data = new CheckoutSessionResponse.DataContainer();
        data.setId("cs_123");
        CheckoutSessionResponse.Attributes attr = new CheckoutSessionResponse.Attributes();
        attr.setCheckout_url("http://pay.me/123");
        data.setAttributes(attr);
        mockResponse.setData(data);

        when(payMongoService.createCheckoutSession(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(mockResponse);

        String payload = "{\"amount\": 100.0, \"orderId\": 500, \"description\": \"Test\"}";

        // Act & Assert
        mockMvc.perform(post("/api/payments/create-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkout_url").value("http://pay.me/123"));
    }

    @Test
    void testCreateSession_Error_ShouldReturn500() throws Exception {
        when(payMongoService.createCheckoutSession(anyString(), anyDouble(), anyString(), anyString()))
                .thenThrow(new RuntimeException("API error"));

        String payload = "{\"amount\": 100.0, \"orderId\": 500}";

        mockMvc.perform(post("/api/payments/create-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isInternalServerError());
    }
}
