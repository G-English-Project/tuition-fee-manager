package com.hyudequeue.genglish.tuition_fee_manager.utility.commandLineRunners;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PayOSConfigService;
import com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl.EmailServiceImpl;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PayOSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.payos.PayOS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Periodically verifies that both PayOS gateways (active + backup) are still
 * usable, so admins find out from a proactive alert instead of discovering an
 * expired/broken gateway only when a student's payment fails.
 * <p>
 * Uses {@code confirmWebhook}, the same safe/non-mutating PayOS call already
 * used by {@code PaymentController#confirmWebhook}, as a lightweight probe:
 * it exercises the clientId/apiKey/checksumKey without creating a real
 * payment link.
 */
@Slf4j
@Component
public class PayOSHealthCheckCron {

    // Must match the webhook URL already registered with PayOS
    // (see PaymentController#confirmWebhook).
    private static final String WEBHOOK_URL = "https://portal-internal.gsenglish.org/api/v1/payment/webhook";
    private static final int MAX_ERROR_LENGTH = 500;

    private final PayOSProperties payOSProperties;
    private final PayOSProperties payOSProperties2;
    private final PayOSConfigService payOSConfigService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService;

    public PayOSHealthCheckCron(
            PayOSProperties payOSProperties,
            @Qualifier("payOSProperties2") PayOSProperties payOSProperties2,
            PayOSConfigService payOSConfigService,
            UserRepository userRepository,
            NotificationService notificationService,
            EmailServiceImpl emailService) {
        this.payOSProperties = payOSProperties;
        this.payOSProperties2 = payOSProperties2;
        this.payOSConfigService = payOSConfigService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Ho_Chi_Minh")
    public void checkGateways() {
        checkGateway(1, payOSProperties, "Payment Gate 1");
        checkGateway(2, payOSProperties2, "Payment Gate 2");
    }

    private void checkGateway(int gateNumber, PayOSProperties properties, String label) {
        boolean healthy;
        String errorMessage = null;

        try {
            PayOS payOS = new PayOS(properties.getClientId(), properties.getApiKey(), properties.getChecksumKey());
            payOS.confirmWebhook(WEBHOOK_URL);
            healthy = true;
            log.info("PayOS health check OK for {}", label);
        } catch (Exception e) {
            healthy = false;
            errorMessage = truncate(e.getMessage() != null ? e.getMessage() : e.toString());
            log.error("PayOS health check FAILED for {}: {}", label, e.toString());
        }

        var result = payOSConfigService.updateGatewayHealth(gateNumber, healthy, errorMessage);

        if (result.justWentDown()) {
            notifyAdmins(NotificationTemplateEnum.PAYOS_GATEWAY_DOWN, label, errorMessage);
        } else if (result.justRecovered()) {
            notifyAdmins(NotificationTemplateEnum.PAYOS_GATEWAY_RECOVERED, label, "");
        }
    }

    private void notifyAdmins(NotificationTemplateEnum template, String gateLabel, String errorMessage) {
        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        if (admins.isEmpty()) {
            return;
        }

        Map<String, String> values = new HashMap<>();
        values.put("gateLabel", gateLabel);
        values.put("errorMessage", errorMessage != null ? errorMessage : "");

        String subject = NotificationTemplateBuilder.buildSubject(template, values);
        String body = NotificationTemplateBuilder.buildBody(template, values);

        for (User admin : admins) {
            notificationService.createNotification(admin.getUserId(), subject, body);
            emailService.sendNotificationEmail(admin.getEmail(), template, values);
        }
    }

    private String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() > MAX_ERROR_LENGTH ? message.substring(0, MAX_ERROR_LENGTH) + "..." : message;
    }
}
