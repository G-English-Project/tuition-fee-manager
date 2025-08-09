package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

import lombok.Data;

@Data
public class PayOSProperties {
    private String clientId;
    private String apiKey;
    private String checksumKey;
    private String url;
    private String domain;
    private String cancelUrl;
    private String returnUrl;
}
