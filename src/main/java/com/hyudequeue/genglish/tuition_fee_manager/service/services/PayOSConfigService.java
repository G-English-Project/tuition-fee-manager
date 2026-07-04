package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payosconfig.response.PayOSConfigResponse;

public interface PayOSConfigService {
    Integer getActiveSecret();
    PayOSConfigResponse getConfig();
    PayOSConfigResponse switchSecret(Integer secretNumber);

    /**
     * Persists the result of a health check for one gateway and reports whether
     * its healthy/unhealthy status just changed, so the caller can decide
     * whether to notify admins (only on transition, not on every check).
     */
    GatewayHealthCheckResult updateGatewayHealth(int gateNumber, boolean healthy, String errorMessage);

    record GatewayHealthCheckResult(int gateNumber, boolean previousHealthy, boolean currentHealthy) {
        public boolean justWentDown() {
            return previousHealthy && !currentHealthy;
        }

        public boolean justRecovered() {
            return !previousHealthy && currentHealthy;
        }
    }
}

