package com.hyudequeue.genglish.tuition_fee_manager.controller.model.payosconfig.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSConfigResponse {
    private Integer activeSecret;
    private String activeSecretLabel;
}

