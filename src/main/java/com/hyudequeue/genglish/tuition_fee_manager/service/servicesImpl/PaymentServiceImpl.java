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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PayOSProperties payOSProperties;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService;
    private final InvoiceNotificationServiceImpl invoiceNotificationService;

    public PaymentServiceImpl(PayOSProperties payOSProperties, PaymentRepository paymentRepository, InvoiceRepository invoiceRepository, UserRepository userRepository, NotificationService notificationService, EmailServiceImpl emailService, InvoiceNotificationServiceImpl invoiceNotificationService) {
        this.payOSProperties = payOSProperties;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.invoiceNotificationService = invoiceNotificationService;
    }

    @Override
    public PaymentPayOSResponse createPayment(CreatePaymentRequest req) throws Exception {
        log.info(">>> [createTransaction] called");

        PayOS payOS = new PayOS(
                payOSProperties.getClientId(),
                payOSProperties.getApiKey(),
                payOSProperties.getChecksumKey()
        );

        Invoice invoice = invoiceRepository.findById(req.getInvoiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Invoice not found"));

        Payment payment = paymentRepository.save(
                Payment.fromCreateRequest(req, invoice, payOSProperties)
        );

        long nowSeconds = System.currentTimeMillis() / 1000;
        long effectiveExpiredAt = (req.getExpiredAt() != null)
                ? req.getExpiredAt()
                : nowSeconds + 15 * 60; // mặc định +15 phút

        if (effectiveExpiredAt <= nowSeconds) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400),
                    "expiredAt must be a future unix time in seconds");
        }

        PaymentData data = Payment.toPaymentData(payment, effectiveExpiredAt);

        CheckoutResponseData checkoutData = payOS.createPaymentLink(data);
        return PaymentPayOSResponse.builder()
                .paymentId(payment.getPaymentId())
                .payOsResponse(checkoutData)
                .build();
    }


    @Override
    public boolean cancelPayment(Long paymentId) throws Exception {
        PayOS payOS = new PayOS(payOSProperties.getClientId(), payOSProperties.getApiKey(), payOSProperties.getChecksumKey());
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
