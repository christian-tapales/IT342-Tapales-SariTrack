package edu.cit.tapales.saritrack.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id")
    private Long vendorId;
    private String title;
    private String message;
    private String type; // INFO, WARNING, SUCCESS
    private Boolean isRead = false;
    private LocalDateTime timestamp;
}
