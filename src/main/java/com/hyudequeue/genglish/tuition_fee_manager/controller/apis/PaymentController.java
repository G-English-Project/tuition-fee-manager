package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentPayOSResponse;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PaymentService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PayOSProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vn.payos.PayOS;
import vn.payos.type.Webhook;

import java.util.Map;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.PaymentEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.PAYMENT_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(PAYMENT_API)
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PayOSProperties payOSProperties;

    @Operation(summary = "Create payment", description = "Create a payment for an invoice and return payment info (payUrl/qr if available).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment created successfully")
    })
    @PostMapping(CREATE)
    public ResponseEntity<ApiResp<PaymentPayOSResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest req) {
        try {
            return ApiResp.success(paymentService.createPayment(req));
        }catch(Exception e){
            log.error("Error creating payment: " + e.toString());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error when creating payment, check server log");
        }
    }

    @Operation(summary = "Cancel payment", description = "Cancel a pending payment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment canceled successfully")
    })
    @PostMapping(CANCEL)
    public ResponseEntity<ApiResp<Boolean>> cancelPayment(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        try{
            return ApiResp.success(paymentService.cancelPayment(paymentId));

        }catch(Exception e){
            log.error("Error cancelling payment: " + e.toString());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error when cancelling payment, check server log");
        }
    }

    @Operation(summary = "Payment webhook (PayOS)", description = "Handle asynchronous webhook callback from PayOS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook processed")
    })
    @PostMapping(WEBHOOK)
    public ResponseEntity<ApiResp<String>> handleWebhook(
            @RequestBody Webhook webhook
    ) {
        try{
            log.info("Webhook called");
            PayOS payOS = new PayOS(payOSProperties.getClientId(), payOSProperties.getApiKey(), payOSProperties.getChecksumKey());
            payOS.verifyPaymentWebhookData(webhook);
            log.info("Pass verify");
            paymentService.handleWebhook(webhook);
            log.info("Webhook success");
            return ApiResp.success("OK");
        }
        catch (Exception e){
            log.error("Error handling webhook: "+ e.toString());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error handling webhook, check server log");
        }
    }

    @Operation(summary = "Get payment by ID", description = "Return a payment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully")
    })
    @GetMapping(GET_BY_ID)
    public ResponseEntity<ApiResp<PaymentResponseDTO>> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        return ApiResp.success(paymentService.getPaymentById(paymentId));
    }

    @Operation(summary = "Get latest payment by invoice", description = "Return the latest payment of an invoice.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest payment retrieved successfully")
    })
    @GetMapping(GET_LATEST_BY_INVOICE)
    public ResponseEntity<ApiResp<PaymentResponseDTO>> getLatestPaymentByInvoice(
            @Parameter(description = "Invoice ID") @PathVariable Long invoiceId) {
        return ApiResp.success(paymentService.getLatestPaymentByInvoiceId(invoiceId));
    }

    @PostMapping(path = "/confirm-webhook")
    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        PayOS payOS = new PayOS(payOSProperties.getClientId(), payOSProperties.getApiKey(), payOSProperties.getChecksumKey());
        try {
            String str = payOS.confirmWebhook("https://genglish-internal.threemusketeer.click/api/v1/payment/webhook");
            response.set("data", objectMapper.valueToTree(str));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }
}
