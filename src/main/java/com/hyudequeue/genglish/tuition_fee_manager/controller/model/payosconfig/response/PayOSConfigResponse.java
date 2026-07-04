package com.hyudequeue.genglish.tuition_fee_manager.controller.model.payosconfig.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSConfigResponse {
    private Integer activeSecret;
    private String activeSecretLabel;
    private LocalDateTime updatedAt;

    private Boolean gate1Healthy;
    private LocalDateTime gate1LastCheckedAt;
    private String gate1LastError;

    private Boolean gate2Healthy;
    private LocalDateTime gate2LastCheckedAt;
    private String gate2LastError;
}

