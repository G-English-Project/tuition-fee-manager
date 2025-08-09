package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentResponse;
import org.springframework.stereotype.Service;
import vn.payos.type.Webhook;

public interface PaymentService {
    PaymentResponse createPayment(CreatePaymentRequest req);
    PaymentResponse cancelPayment(Long paymentId);
    void handleWebhook(Webhook webhook);
    PaymentResponse getPaymentById(Long paymentId);
    PaymentResponse getLatestPaymentByInvoiceId(Long invoiceId);
}
