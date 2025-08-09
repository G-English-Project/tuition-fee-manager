package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;

public final class PaymentEndpoints {
    private PaymentEndpoints() {}

    public static final String CREATE = "/create";
    public static final String CANCEL = "/{paymentId}/cancel";
    public static final String GET_BY_ID = "/{paymentId}";
    public static final String GET_LATEST_BY_INVOICE = "/invoice/{invoiceId}/latest";
    public static final String WEBHOOK = "/webhook";
}
