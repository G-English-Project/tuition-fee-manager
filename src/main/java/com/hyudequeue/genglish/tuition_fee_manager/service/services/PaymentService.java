package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentPayOSResponse;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Payment;
import vn.payos.type.Webhook;

public interface PaymentService {
    PaymentPayOSResponse createPayment(CreatePaymentRequest req) throws Exception;
    boolean cancelPayment(Long paymentId) throws Exception;
    void handleWebhook(Webhook webhook);
    PaymentResponseDTO getPaymentById(Long paymentId);
    PaymentResponseDTO getLatestPaymentByInvoiceId(Long invoiceId);
}
