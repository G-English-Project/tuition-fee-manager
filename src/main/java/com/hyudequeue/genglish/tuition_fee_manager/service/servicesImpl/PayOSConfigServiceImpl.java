package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payosconfig.response.PayOSConfigResponse;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ResourceTypeEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.PayOSConfig;
import com.hyudequeue.genglish.tuition_fee_manager.repository.PayOSConfigRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ActionLogService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PayOSConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayOSConfigServiceImpl implements PayOSConfigService {

    private final PayOSConfigRepository payOSConfigRepository;
    private final ActionLogService actionLogService;

    @Override
    public Integer getActiveSecret() {
        PayOSConfig config = getOrCreateConfig();
        return config.getActiveSecret();
    }

    @Override
    public PayOSConfigResponse getConfig() {
        PayOSConfig config = getOrCreateConfig();
        return PayOSConfigResponse.builder()
                .activeSecret(config.getActiveSecret())
                .activeSecretLabel(getSecretLabel(config.getActiveSecret()))
                .updatedAt(config.getUpdatedAt())
                .gate1Healthy(config.getGate1Healthy())
                .gate1LastCheckedAt(config.getGate1LastCheckedAt())
                .gate1LastError(config.getGate1LastError())
                .gate2Healthy(config.getGate2Healthy())
                .gate2LastCheckedAt(config.getGate2LastCheckedAt())
                .gate2LastError(config.getGate2LastError())
                .build();
    }

    @Override
    @Transactional
    public GatewayHealthCheckResult updateGatewayHealth(int gateNumber, boolean healthy, String errorMessage) {
        PayOSConfig config = getOrCreateConfig();
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));

        boolean previousHealthy;
        if (gateNumber == 1) {
            previousHealthy = config.getGate1Healthy() == null || config.getGate1Healthy();
            config.setGate1Healthy(healthy);
            config.setGate1LastCheckedAt(now);
            config.setGate1LastError(errorMessage);
        } else {
            previousHealthy = config.getGate2Healthy() == null || config.getGate2Healthy();
            config.setGate2Healthy(healthy);
            config.setGate2LastCheckedAt(now);
            config.setGate2LastError(errorMessage);
        }
        payOSConfigRepository.save(config);

        return new GatewayHealthCheckResult(gateNumber, previousHealthy, healthy);
    }

    @Override
    @Transactional
    public PayOSConfigResponse switchSecret(Integer secretNumber) {
        if (secretNumber == null || (secretNumber != 1 && secretNumber != 2)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Secret number must be 1 or 2"
            );
        }

        PayOSConfig config = getOrCreateConfig();
        config.setActiveSecret(secretNumber);
        config.setUpdatedAt(LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")));
        payOSConfigRepository.save(config);

        log.info("PayOS secret switched to: {}", secretNumber);
        actionLogService.switched(
                ResourceTypeEnum.PAYOS_CONFIG,
                config.getId(),
                getSecretLabel(secretNumber)
        );

        return PayOSConfigResponse.builder()
                .activeSecret(config.getActiveSecret())
                .activeSecretLabel(getSecretLabel(config.getActiveSecret()))
                .updatedAt(config.getUpdatedAt())
                .build();
    }

    private PayOSConfig getOrCreateConfig() {
        return payOSConfigRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    // Initialize with default secret 1 (Payment Gate 1)
                    PayOSConfig defaultConfig = PayOSConfig.builder()
                            .activeSecret(1)
                            .updatedAt(LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")))
                            .build();
                    return payOSConfigRepository.save(defaultConfig);
                });
    }

    private String getSecretLabel(Integer secretNumber) {
        if (secretNumber == null) {
            return "Payment Gate 1";
        }
        return secretNumber == 1 ? "Payment Gate 1" : "Payment Gate 2";
    }
}

