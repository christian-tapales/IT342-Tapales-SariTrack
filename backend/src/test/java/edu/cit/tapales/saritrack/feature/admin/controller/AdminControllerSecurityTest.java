package edu.cit.tapales.saritrack.feature.admin.controller;

import edu.cit.tapales.saritrack.core.exception.GlobalExceptionHandler;
import edu.cit.tapales.saritrack.core.security.JwtFilter;
import edu.cit.tapales.saritrack.core.security.JwtUtils;
import edu.cit.tapales.saritrack.core.security.SecurityConfig;
import edu.cit.tapales.saritrack.feature.admin.dto.PlatformStatsDTO;
import edu.cit.tapales.saritrack.feature.admin.dto.VendorAnalyticsDTO;
import edu.cit.tapales.saritrack.feature.admin.service.AdminService;
import edu.cit.tapales.saritrack.feature.auth.repository.UserRepository;
import edu.cit.tapales.saritrack.feature.auth.service.CustomOAuth2UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtFilter jwtFilter;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
        org.mockito.Mockito.doAnswer(invocation -> {
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtFilter).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPlatformStats_AsAdmin_ShouldReturnOk() throws Exception {
        PlatformStatsDTO stats = new PlatformStatsDTO();
        stats.setTotalVendors(5);
        stats.setTotalPlatformSales(25000.0);
        when(adminService.getPlatformStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVendors").value(5))
                .andExpect(jsonPath("$.totalPlatformSales").value(25000.0));
    }

    @Test
    @WithMockUser(roles = "VENDOR")
    void testGetPlatformStats_AsVendor_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDOR")
    void testGetVendorAnalytics_AsVendor_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/vendors/analytics"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPlatformStats_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized());
    }
}
