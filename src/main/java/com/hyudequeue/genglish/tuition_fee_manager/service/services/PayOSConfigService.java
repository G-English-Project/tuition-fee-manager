package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payosconfig.response.PayOSConfigResponse;

public interface PayOSConfigService {
    Integer getActiveSecret();
    PayOSConfigResponse getConfig();
    PayOSConfigResponse switchSecret(Integer secretNumber);
}

