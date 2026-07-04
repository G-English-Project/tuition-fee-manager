package com.hyudequeue.genglish.tuition_fee_manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payos_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayOSConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer activeSecret; // 1 or 2

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Health-check status for each gateway, updated by PayOSHealthCheckCron
    private Boolean gate1Healthy;
    private LocalDateTime gate1LastCheckedAt;
    @Column(columnDefinition = "TEXT")
    private String gate1LastError;

    private Boolean gate2Healthy;
    private LocalDateTime gate2LastCheckedAt;
    @Column(columnDefinition = "TEXT")
    private String gate2LastError;

    @PrePersist
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
    }
}

