package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentPayOSResponse;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Payment;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.PaymentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PaymentService;
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

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PayOSProperties payOSProperties;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;


    public PaymentServiceImpl(PayOSProperties payOSProperties, PaymentRepository paymentRepository, InvoiceRepository invoiceRepository) {
        this.payOSProperties = payOSProperties;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public PaymentPayOSResponse createPayment(CreatePaymentRequest req) throws Exception {
        log.info(">>> [createTransaction] called");
        PayOS payOS = new PayOS(payOSProperties.getClientId(), payOSProperties.getApiKey(), payOSProperties.getChecksumKey());
        Invoice invoice = invoiceRepository.findById(req.getInvoiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),"Invoice not found"));

        Payment payment = paymentRepository.save(Payment.fromCreateRequest(req, invoice, payOSProperties));
        PaymentData data = Payment.toPaymentData(payment);

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
        }
        else {
            payment.setStatus(PaymentStatusEnum.CANCELLED);
        }
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
