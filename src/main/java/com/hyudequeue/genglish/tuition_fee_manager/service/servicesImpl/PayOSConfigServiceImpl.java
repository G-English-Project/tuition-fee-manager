package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payosconfig.response.PayOSConfigResponse;
import com.hyudequeue.genglish.tuition_fee_manager.entities.PayOSConfig;
import com.hyudequeue.genglish.tuition_fee_manager.repository.PayOSConfigRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PayOSConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayOSConfigServiceImpl implements PayOSConfigService {

    private final PayOSConfigRepository payOSConfigRepository;

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
                .build();
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
        config.setUpdatedAt(LocalDateTime.now());
        payOSConfigRepository.save(config);

        log.info("PayOS secret switched to: {}", secretNumber);

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
                            .updatedAt(LocalDateTime.now())
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

