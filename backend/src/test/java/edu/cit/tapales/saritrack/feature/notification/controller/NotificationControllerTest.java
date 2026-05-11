package edu.cit.tapales.saritrack.feature.notification.controller;

import edu.cit.tapales.saritrack.feature.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void testGetNotifications_ShouldCallService() throws Exception {
        when(notificationService.getVendorNotifications(100L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications?vendorId=100"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).getVendorNotifications(100L);
    }

    @Test
    void testSyncNotifications_ShouldCallService() throws Exception {
        mockMvc.perform(post("/api/notifications/sync?vendorId=100"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).syncLowStockNotifications(100L);
    }

    @Test
    void testMarkAsRead_ShouldCallService() throws Exception {
        mockMvc.perform(post("/api/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    void testMarkAllAsRead_ShouldCallService() throws Exception {
        mockMvc.perform(post("/api/notifications/read-all?vendorId=100"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAllAsRead(100L);
    }
}
