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
}

