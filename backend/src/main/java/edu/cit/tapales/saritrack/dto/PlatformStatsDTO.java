package edu.cit.tapales.saritrack.dto;

import lombok.Data;

@Data
public class PlatformStatsDTO {
    private Double totalPlatformSales;
    private Integer totalVendors;
    private Long totalSKUs;
    private Long totalStock;
    private Double systemHealth; 
    private java.util.List<java.util.Map<String, Object>> weeklySales;
    private java.util.List<java.util.Map<String, Object>> topVendors;
    private java.util.List<java.util.Map<String, Object>> recentTransactions;
}
