package edu.cit.tapales.saritrack.feature.admin.controller;

import edu.cit.tapales.saritrack.feature.admin.dto.PlatformStatsDTO;
import edu.cit.tapales.saritrack.feature.admin.dto.VendorAnalyticsDTO;
import edu.cit.tapales.saritrack.feature.admin.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

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
    void testGetPlatformStats_ShouldReturnStats() throws Exception {
        PlatformStatsDTO stats = new PlatformStatsDTO();
        stats.setTotalVendors(10);
        stats.setTotalPlatformSales(5000.0);
        
        when(adminService.getPlatformStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVendors").value(10))
                .andExpect(jsonPath("$.totalPlatformSales").value(5000.0));
    }

    @Test
    void testGetVendorAnalytics_ShouldReturnList() throws Exception {
        VendorAnalyticsDTO analytics = new VendorAnalyticsDTO();
        analytics.setName("Test Vendor");
        analytics.setTotalSales(1200.0);
        
        when(adminService.getAllVendorAnalytics()).thenReturn(List.of(analytics));

        mockMvc.perform(get("/api/admin/vendors/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Vendor"))
                .andExpect(jsonPath("$[0].totalSales").value(1200.0));
    }
}
