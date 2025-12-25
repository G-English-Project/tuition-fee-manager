package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentPayOSResponse;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Payment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.PaymentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PayOSConfigService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PaymentService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PayOSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PayOSProperties payOSProperties;
    private final PayOSProperties payOSProperties2;
    private final PayOSConfigService payOSConfigService;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService;
    private final InvoiceNotificationServiceImpl invoiceNotificationService;

    public PaymentServiceImpl(
            PayOSProperties payOSProperties,
            @org.springframework.beans.factory.annotation.Qualifier("payOSProperties2") PayOSProperties payOSProperties2,
            PayOSConfigService payOSConfigService,
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            EmailServiceImpl emailService,
            InvoiceNotificationServiceImpl invoiceNotificationService) {
        this.payOSProperties = payOSProperties;
        this.payOSProperties2 = payOSProperties2;
        this.payOSConfigService = payOSConfigService;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.invoiceNotificationService = invoiceNotificationService;
    }

    private PayOSProperties getActivePayOSProperties() {
        Integer activeSecret = payOSConfigService.getActiveSecret();
        return activeSecret != null && activeSecret == 2 ? payOSProperties2 : payOSProperties;
    }

    @Override
    public PaymentPayOSResponse createPayment(CreatePaymentRequest req) throws Exception {
        log.info(">>> [createTransaction] called");

        // 1) Validate cơ bản
        Invoice invoice = invoiceRepository.findById(req.getInvoiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        if (invoice.getStatus() == InvoiceStatusEnum.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice is already PAID");
        }
        if (invoice.getTotalAmount() == null || invoice.getTotalAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice totalAmount must be > 0");
        }

        if (req.getBuyerName() == null || req.getBuyerName().isBlank()) {
            req.setBuyerName(invoice.getUserName() != null && !invoice.getUserName().isBlank()
                    ? invoice.getUserName()
                    : (invoice.getUser() != null ? invoice.getUser().getFullName() : "HOC VIEN"));
        }
        if (req.getBuyerEmail() == null || req.getBuyerEmail().isBlank()) {
            req.setBuyerEmail(invoice.getUser() != null ? invoice.getUser().getEmail() : null);
        }
        if (req.getBuyerPhone() == null || req.getBuyerPhone().isBlank()) {
            req.setBuyerPhone(invoice.getUser() != null ? invoice.getUser().getPhone() : null);
        }

        PayOSProperties activeProperties = getActivePayOSProperties();
        Payment payment = paymentRepository.save(Payment.fromCreateRequest(req, invoice, activeProperties));

        long nowSeconds = System.currentTimeMillis() / 1000;
        long ttlSeconds = (req.getExpiredAt() != null) ? req.getExpiredAt() : 15 * 60;
        if (ttlSeconds <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "expiredAt (seconds) must be a positive number");
        }
        long effectiveExpiredAt = nowSeconds + ttlSeconds;

        PayOS payOS = new PayOS(activeProperties.getClientId(), activeProperties.getApiKey(), activeProperties.getChecksumKey());
        PaymentData data = Payment.toPaymentData(payment, effectiveExpiredAt);
        CheckoutResponseData checkoutData = payOS.createPaymentLink(data);
        return PaymentPayOSResponse.builder()
                .paymentId(payment.getPaymentId())
                .payOsResponse(checkoutData)
                .build();
    }

    @Override
    public boolean cancelPayment(Long paymentId) throws Exception {
        PayOSProperties activeProperties = getActivePayOSProperties();
        PayOS payOS = new PayOS(activeProperties.getClientId(), activeProperties.getApiKey(), activeProperties.getChecksumKey());
        PaymentLinkData data = payOS.cancelPaymentLink(paymentId, "Cancelled");
        return !data.getCanceledAt().isBlank();
    }

    @Override
    public void handleWebhook(Webhook webhook) {
        log.info("Webhook status: {}", webhook.getSuccess() ? "SUCCESS" : "FAILED");
        Payment payment = paymentRepository.findById(webhook.getData().getOrderCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),"Payment not found"));
        Invoice invoice = payment.getInvoice();
        if(webhook.getSuccess()){
            payment.setStatus(PaymentStatusEnum.PAID);
            invoice.setPaidAt(LocalDateTime.now());
            invoice.setUpdatedAt(LocalDateTime.now());
            invoice.setStatus(InvoiceStatusEnum.PAID);
            invoiceNotificationService.notifyPaymentSuccess(invoice, payment);
        }
        else {
            payment.setStatus(PaymentStatusEnum.CANCELLED);
        }
        paymentRepository.save(payment);
        invoiceRepository.save(invoice);
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        return PaymentResponseDTO.toResponseDTO(paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),"Payment not found")));
    }

    @Override
    public PaymentResponseDTO getLatestPaymentByInvoiceId(Long invoiceId) {
        Payment latestPayment = paymentRepository
                .findTopByInvoice_InvoiceIdOrderByCreatedAtDesc(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No payment found for invoice ID: " + invoiceId
                ));

        return PaymentResponseDTO.toResponseDTO(latestPayment);
    }

}
