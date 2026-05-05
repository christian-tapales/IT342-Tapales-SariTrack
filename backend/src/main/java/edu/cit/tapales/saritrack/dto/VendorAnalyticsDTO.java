package edu.cit.tapales.saritrack.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VendorAnalyticsDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime registrationDate;
    private Double totalSales;
    private Integer orderCount;
    private String status; // Active, New, Inactive based on sales
}
