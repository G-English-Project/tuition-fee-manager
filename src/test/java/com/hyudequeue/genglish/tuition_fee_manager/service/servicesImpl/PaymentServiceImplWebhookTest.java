package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceNotifyDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Payment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.PaymentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PayOSConfigService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PayOSProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import vn.payos.type.Webhook;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceImplWebhookTest {

    private PaymentServiceImpl paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Long, Payment> paymentDb = new HashMap<>();
    private final Map<Long, Invoice> invoiceDb = new HashMap<>();
    private final List<InvoiceNotifyDTO> successNotifications = new ArrayList<>();

    static class TestInvoiceNotificationService extends InvoiceNotificationServiceImpl {
        private final List<InvoiceNotifyDTO> list;

        public TestInvoiceNotificationService(List<InvoiceNotifyDTO> list) {
            super(null, null, null);
            this.list = list;
        }

        @Override
        public void notifyPaymentSuccess(InvoiceNotifyDTO dto) {
            list.add(dto);
        }
    }

    @BeforeEach
    void setUp() {
        paymentDb.clear();
        invoiceDb.clear();
        successNotifications.clear();

        PaymentRepository paymentRepository = (PaymentRepository) Proxy.newProxyInstance(
                PaymentRepository.class.getClassLoader(),
                new Class<?>[]{PaymentRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        Long id = (Long) args[0];
                        return Optional.ofNullable(paymentDb.get(id));
                    }
                    if ("findTopByInvoice_InvoiceIdOrderByCreatedAtDesc".equals(method.getName())) {
                        Long invoiceId = (Long) args[0];
                        return paymentDb.values().stream()
                                .filter(p -> p.getInvoice() != null && Objects.equals(p.getInvoice().getInvoiceId(), invoiceId))
                                .findFirst();
                    }
                    if ("save".equals(method.getName())) {
                        Payment p = (Payment) args[0];
                        paymentDb.put(p.getPaymentId(), p);
                        return p;
                    }
                    return null;
                }
        );

        InvoiceRepository invoiceRepository = (InvoiceRepository) Proxy.newProxyInstance(
                InvoiceRepository.class.getClassLoader(),
                new Class<?>[]{InvoiceRepository.class},
                (proxy, method, args) -> {
                    if ("save".equals(method.getName())) {
                        Invoice inv = (Invoice) args[0];
                        invoiceDb.put(inv.getInvoiceId(), inv);
                        return inv;
                    }
                    return null;
                }
        );

        TestInvoiceNotificationService invoiceNotificationService = new TestInvoiceNotificationService(successNotifications);

        paymentService = new PaymentServiceImpl(
                new PayOSProperties(),
                new PayOSProperties(),
                new PayOSProperties(),
                null,
                paymentRepository,
                invoiceRepository,
                null,
                null,
                null,
                invoiceNotificationService
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
                    "signature": "sig123"
                }
                """, orderCode, amount, desc);
        return objectMapper.readValue(json, Webhook.class);
    }

    @Test
    void handleWebhook_success_updatesPaymentAndInvoiceToPaid() throws Exception {
        User student = User.builder().userId(50L).fullName("Test Student").email("student@test.com").build();
        Invoice invoice = Invoice.builder()
                .invoiceId(1836L)
                .user(student)
                .status(InvoiceStatusEnum.UNPAID)
                .totalAmount(1800000)
                .build();

        Payment payment = Payment.builder()
                .paymentId(101L)
                .invoice(invoice)
                .amount(1800000)
                .status(PaymentStatusEnum.PENDING)
                .build();

        paymentDb.put(101L, payment);
        invoiceDb.put(1836L, invoice);

        Webhook webhook = createWebhook(101L, 1800000, "CSQWJ9BR2H1 Danh Olive 001836");

        paymentService.handleWebhook(webhook);

        assertEquals(PaymentStatusEnum.PAID, payment.getStatus());
        assertEquals(InvoiceStatusEnum.PAID, invoice.getStatus());
        assertNotNull(invoice.getPaidAt());
        assertEquals(1, successNotifications.size(), "Notification should be sent once");
    }

    @Test
    void handleWebhook_notFound_throws404() throws Exception {
        Webhook webhook = createWebhook(9999L, 100000, "Unknown payment");

        assertThrows(ResponseStatusException.class, () -> paymentService.handleWebhook(webhook));
    }
}
