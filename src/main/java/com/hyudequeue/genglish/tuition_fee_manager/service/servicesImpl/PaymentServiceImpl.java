package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentResponse;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PaymentService;
import org.springframework.stereotype.Service;
import vn.payos.type.Webhook;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public PaymentResponse createPayment(CreatePaymentRequest req) {
        return null;
    }

    @Override
    public PaymentResponse cancelPayment(Long paymentId) {
        return null;
    }

    @Override
    public void handleWebhook(Webhook webhook) {

    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        return null;
    }

    @Override
    public PaymentResponse getLatestPaymentByInvoiceId(Long invoiceId) {
        return null;
    }
}
