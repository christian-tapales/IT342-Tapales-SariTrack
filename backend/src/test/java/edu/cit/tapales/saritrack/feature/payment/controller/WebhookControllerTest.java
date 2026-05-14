package edu.cit.tapales.saritrack.feature.payment.controller;

import edu.cit.tapales.saritrack.feature.order.service.OrderService;
import edu.cit.tapales.saritrack.feature.payment.entity.Payment;
import edu.cit.tapales.saritrack.feature.payment.repository.PaymentRepository;
import edu.cit.tapales.saritrack.feature.payment.service.PayMongoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayMongoService payMongoService;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @MockitoBean
    private OrderService orderService;

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
    void testWebhook_InvalidSignature_ShouldReturn401() throws Exception {
        when(payMongoService.isSignatureValid(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/webhooks/paymongo")
                .header("Paymongo-Signature", "invalid")
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testWebhook_ValidPaidEvent_ShouldFinalizeOrder() throws Exception {
        when(payMongoService.isSignatureValid(anyString(), anyString())).thenReturn(true);
        
        String json = "{\"data\":{\"attributes\":{\"type\":\"checkout_session.payment.paid\",\"data\":{\"id\":\"cs_123\"}}}}";
        
        Payment payment = new Payment();
        payment.setOrderId(500L);
        payment.setPaymongoId("cs_123");
        
        when(paymentRepository.findByPaymongoId("cs_123")).thenReturn(Optional.of(payment));

        mockMvc.perform(post("/api/webhooks/paymongo")
                .header("Paymongo-Signature", "valid")
                .content(json))
                .andExpect(status().isOk());

        verify(orderService).finalizeDigitalOrder(500L);
        verify(paymentRepository).save(payment);
    }
}
