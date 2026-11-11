package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentPayOSResponse;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PayOSConfigService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PaymentService;
import com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl.EmailServiceImpl;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PayOSProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vn.payos.type.Webhook;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PaymentControllerWebhookTest {

    private PayOSProperties payOSProperties1;
    private PayOSProperties payOSProperties2;
    private PayOSProperties payOSProperties3;

    private PaymentController paymentController;
    private TestEmailService testEmailService;
    private List<String> inAppNotifications;
    private final ObjectMapper objectMapper = new ObjectMapper();

    static class TestEmailService extends EmailServiceImpl {
        public final List<String> sentTo = new ArrayList<>();
        public final List<NotificationTemplateEnum> sentTemplates = new ArrayList<>();
        public final List<Map<String, String>> sentValues = new ArrayList<>();

        public TestEmailService() {
            super(null);
        }

        @Override
        public void sendNotificationEmail(String receiverEmail, NotificationTemplateEnum template, Map<String, String> values) {
            sentTo.add(receiverEmail);
            sentTemplates.add(template);
            sentValues.add(values);
        }
    }

    @BeforeEach
    void setUp() {
        payOSProperties1 = new PayOSProperties();
        payOSProperties1.setClientId("client-1");
        payOSProperties1.setApiKey("api-1");
        payOSProperties1.setChecksumKey("chk-1");

        payOSProperties2 = new PayOSProperties();
        payOSProperties2.setClientId("client-2");
        payOSProperties2.setApiKey("api-2");
        payOSProperties2.setChecksumKey("chk-2");

        payOSProperties3 = new PayOSProperties();
        payOSProperties3.setClientId("client-3");
        payOSProperties3.setApiKey("api-3");
        payOSProperties3.setChecksumKey("chk-3");

        testEmailService = new TestEmailService();
        inAppNotifications = new ArrayList<>();

        PaymentService paymentService = (PaymentService) Proxy.newProxyInstance(
                PaymentService.class.getClassLoader(),
                new Class<?>[]{PaymentService.class},
                (proxy, method, args) -> null
        );

        PayOSConfigService payOSConfigService = (PayOSConfigService) Proxy.newProxyInstance(
                PayOSConfigService.class.getClassLoader(),
                new Class<?>[]{PayOSConfigService.class},
                (proxy, method, args) -> {
                    if ("getActiveSecret".equals(method.getName())) {
                        return 3; // Payment Gate 3 active
                    }
                    return null;
                }
        );

        UserRepository userRepository = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> {
                    if ("findByRole".equals(method.getName())) {
                        User admin = User.builder()
                                .userId(10L)
                                .email("admin@gsenglish.org")
                                .role(RoleEnum.ADMIN)
                                .build();
                        return List.of(admin);
                    }
                    return Collections.emptyList();
                }
        );

        NotificationService notificationService = (NotificationService) Proxy.newProxyInstance(
                NotificationService.class.getClassLoader(),
                new Class<?>[]{NotificationService.class},
                (proxy, method, args) -> {
                    if ("createNotification".equals(method.getName())) {
                        inAppNotifications.add(args[0] + ": " + args[1]);
                    }
                    return null;
                }
        );

        paymentController = new PaymentController(
                paymentService,
                payOSProperties1,
                payOSProperties2,
                payOSProperties3,
                payOSConfigService,
                userRepository,
                notificationService,
                testEmailService
        );
    }

    private Webhook createWebhook(long orderCode, int amount, String desc) throws Exception {
        String json = String.format("""
                {
                    "code": "00",
                    "desc": "success",
                    "success": true,
                    "data": {
                        "orderCode": %d,
                        "amount": %d,
                        "description": "%s",
                        "accountNumber": "12345678",
                        "reference": "REF123456",
                        "transactionDateTime": "2026-11-10 20:00:00",
                        "currency": "VND",
                        "paymentLinkId": "plink123",
                        "code": "00",
                        "desc": "success"
                    },
                    "signature": "invalid_sig"
                }
                """, orderCode, amount, desc);
        return objectMapper.readValue(json, Webhook.class);
    }

    @Test
    void handleWebhook_whenVerificationFails_shouldNotifyAdminsAndThrow500() throws Exception {
        Webhook webhook = createWebhook(1836L, 1800000, "CSQWJ9BR2H1 Danh Olive 001836");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            paymentController.handleWebhook(webhook);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getStatusCode().value());

        // Verify admin notification
        assertEquals(2, testEmailService.sentTo.size(), "Admin and quanghuy should receive email");
        assertTrue(testEmailService.sentTo.contains("admin@gsenglish.org"));
        assertTrue(testEmailService.sentTo.contains("quanghuy01062004@gmail.com"));
        assertEquals(NotificationTemplateEnum.PAYMENT_WEBHOOK_FAILED, testEmailService.sentTemplates.get(0));

        Map<String, String> values = testEmailService.sentValues.get(0);
        assertEquals("Payment Gate 3", values.get("gateLabel"));
        assertEquals("1836", values.get("orderCode"));
        assertEquals("1,800,000", values.get("amount"));
        assertEquals("CSQWJ9BR2H1 Danh Olive 001836", values.get("description"));

        // Verify in-app notification
        assertEquals(1, inAppNotifications.size());
        assertTrue(inAppNotifications.get(0).contains("10:"));
    }
}
