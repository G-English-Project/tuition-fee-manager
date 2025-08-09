package com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.payos.type.CheckoutResponseData;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private CheckoutResponseData payOsResponse;
}
