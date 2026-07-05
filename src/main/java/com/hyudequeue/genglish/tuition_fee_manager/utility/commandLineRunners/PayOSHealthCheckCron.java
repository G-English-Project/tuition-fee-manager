package com.hyudequeue.genglish.tuition_fee_manager.utility.commandLineRunners;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Payment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.PaymentRepository;
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
import java.util.Optional;

/**
 * Periodically verifies that the active PayOS gateway (Gate 2) is still
 * usable, so admins find out from a proactive alert instead of discovering an
 * expired/broken gateway only when a student's payment fails.
 * <p>
 * <b>Incident history (2026-07-05):</b> an earlier version of this class used
 * {@code confirmWebhook}, which is NOT read-only — calling it makes PayOS
 * send a real test webhook (fixed sample {@code orderCode: 123}) to our
 * webhook URL. That test webhook was accepted by
 * {@code PaymentServiceImpl#handleWebhook} as a genuine payment because
 * payment_id 123 happened to belong to a real, still-unpaid payment, which
 * got incorrectly marked PAID as a result.
 * <p>
 * This version uses {@link PayOS#getPaymentLinkInformation(Long)} instead,
 * which issues a plain HTTP GET to PayOS (read-only lookup of an existing
 * payment's status) and never causes PayOS to call back into our webhook
 * endpoint. It cannot mutate any data on either side.
 */
@Slf4j
@Component
public class PayOSHealthCheckCron {

    private static final int MAX_ERROR_LENGTH = 500;

    private final PayOSProperties payOSProperties2;
    private final PayOSConfigService payOSConfigService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService;

    public PayOSHealthCheckCron(
            @Qualifier("payOSProperties2") PayOSProperties payOSProperties2,
            PayOSConfigService payOSConfigService,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            EmailServiceImpl emailService) {
        this.payOSProperties2 = payOSProperties2;
        this.payOSConfigService = payOSConfigService;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Ho_Chi_Minh")
    public void checkGateways() {
        checkGateway(2, payOSProperties2, "Payment Gate 2");
    }

    private void checkGateway(int gateNumber, PayOSProperties properties, String label) {
        Optional<Payment> latestPayment = paymentRepository.findTopByOrderByCreatedAtDesc();
        if (latestPayment.isEmpty()) {
            log.info("PayOS health check skipped for {}: no payment exists yet to probe.", label);
            return;
        }

        boolean healthy;
        String errorMessage = null;

        try {
            PayOS payOS = new PayOS(properties.getClientId(), properties.getApiKey(), properties.getChecksumKey());
            // Read-only GET lookup — does not create, cancel, or notify anything.
            payOS.getPaymentLinkInformation(latestPayment.get().getPaymentId());
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
