package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.dto.VendorAnalyticsDTO;
import edu.cit.tapales.saritrack.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public edu.cit.tapales.saritrack.dto.PlatformStatsDTO getPlatformStats() {
        return adminService.getPlatformStats();
    }

    @GetMapping("/vendors/analytics")
    public List<VendorAnalyticsDTO> getVendorAnalytics() {
        return adminService.getAllVendorAnalytics();
    }
}
