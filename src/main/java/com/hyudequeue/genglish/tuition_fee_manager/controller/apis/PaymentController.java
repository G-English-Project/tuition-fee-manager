package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentPayOSResponse;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response.PaymentResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PayOSConfigService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PaymentService;
import com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl.EmailServiceImpl;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PayOSProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vn.payos.PayOS;
import vn.payos.type.Webhook;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.PaymentEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.PAYMENT_API;

@RestController
@RequestMapping(PAYMENT_API)
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PayOSProperties payOSProperties;
    private final PayOSProperties payOSProperties2;
    private final PayOSProperties payOSProperties3;
    private final PayOSConfigService payOSConfigService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService;

    public PaymentController(
            PaymentService paymentService,
            PayOSProperties payOSProperties,
            @Qualifier("payOSProperties2") PayOSProperties payOSProperties2,
            @Qualifier("payOSProperties3") PayOSProperties payOSProperties3,
            PayOSConfigService payOSConfigService,
            UserRepository userRepository,
            NotificationService notificationService,
            EmailServiceImpl emailService) {
        this.paymentService = paymentService;
        this.payOSProperties = payOSProperties;
        this.payOSProperties2 = payOSProperties2;
        this.payOSProperties3 = payOSProperties3;
        this.payOSConfigService = payOSConfigService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

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
        Integer activeSecret = payOSConfigService.getActiveSecret();
        PayOSProperties activeProperties = (activeSecret != null && activeSecret == 3) ? payOSProperties3
                : (activeSecret != null && activeSecret == 2) ? payOSProperties2
                : payOSProperties;

        try {
            log.info("Webhook called with activeSecret: {}", activeSecret);
            PayOS payOS = new PayOS(activeProperties.getClientId(), activeProperties.getApiKey(), activeProperties.getChecksumKey());
            payOS.verifyPaymentWebhookData(webhook);
            log.info("Pass verify");
            paymentService.handleWebhook(webhook);
            log.info("Webhook success");
            return ApiResp.success("OK");
        } catch (Exception e) {
            log.error("Error handling webhook: " + e.toString(), e);
            notifyWebhookError(webhook, e.getMessage(), activeSecret);
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error handling webhook, check server log");
        }
    }

    private void notifyWebhookError(Webhook webhook, String rawErrorMessage, Integer activeSecret) {
        try {
            Set<String> recipientEmails = new HashSet<>();
            recipientEmails.add("quanghuy01062004@gmail.com");

            List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
            if (admins != null) {
                for (User admin : admins) {
                    if (admin.getEmail() != null && !admin.getEmail().isBlank()) {
                        recipientEmails.add(admin.getEmail().trim());
                    }
                }
            }

            String orderCode = "N/A";
            String amount = "0";
            String description = "N/A";
            String reference = "N/A";

            if (webhook != null && webhook.getData() != null) {
                var data = webhook.getData();
                if (data.getOrderCode() != null && data.getOrderCode() != 0) {
                    orderCode = String.valueOf(data.getOrderCode());
                }
                amount = String.format("%,d", data.getAmount());
                if (data.getDescription() != null && !data.getDescription().isBlank()) {
                    description = data.getDescription();
                }
                if (data.getReference() != null && !data.getReference().isBlank()) {
                    reference = data.getReference();
                }
            }

            String gateLabel = (activeSecret != null && activeSecret == 3) ? "Payment Gate 3"
                    : (activeSecret != null && activeSecret == 2) ? "Payment Gate 2"
                    : "Payment Gate 1";

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
            String errorMessage = (rawErrorMessage != null && !rawErrorMessage.isBlank())
                    ? rawErrorMessage
                    : "Lỗi không xác định khi xác thực hoặc xử lý Webhook";

            Map<String, String> values = new HashMap<>();
            values.put("gateLabel", gateLabel);
            values.put("orderCode", orderCode);
            values.put("amount", amount);
            values.put("description", description);
            values.put("reference", reference);
            values.put("timestamp", timestamp);
            values.put("errorMessage", errorMessage);

            String subject = NotificationTemplateBuilder.buildSubject(NotificationTemplateEnum.PAYMENT_WEBHOOK_FAILED, values);
            String body = NotificationTemplateBuilder.buildBody(NotificationTemplateEnum.PAYMENT_WEBHOOK_FAILED, values);

            // Gửi in-app notification cho Admin
            if (admins != null) {
                for (User admin : admins) {
                    try {
                        if (admin.getUserId() != null) {
                            notificationService.createNotification(admin.getUserId(), subject, body);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            // Gửi email cho các Admin và quanghuy01062004@gmail.com
            for (String email : recipientEmails) {
                try {
                    emailService.sendNotificationEmail(email, NotificationTemplateEnum.PAYMENT_WEBHOOK_FAILED, values);
                    log.info("Sent webhook failure alert email to: {}", email);
                } catch (Exception mailEx) {
                    log.error("Failed to send webhook failure alert email to {}: {}", email, mailEx.getMessage());
                }
            }
        } catch (Exception ex) {
            log.error("Failed to execute notifyWebhookError: {}", ex.getMessage(), ex);
        }
    }

//    @Operation(summary = "Payment webhook (PayOS)", description = "Handle asynchronous webhook callback from PayOS.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Webhook processed")
//    })
//    @PostMapping(WEBHOOK)
//    public ResponseEntity<ApiResp<String>> handleWebhookConfirmPayOs(
//            @RequestBody Webhook webhook
//    ) {
//        try{
//            log.info("Webhook called");
//            log.info("Pass verify");
//            log.info("Webhook success");
//            return ApiResp.success("OK");
//        }
//        catch (Exception e){
//            log.error("Error handling webhook: "+ e.toString());
//            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error handling webhook, check server log");
//        }
//    }

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
            String str = payOS.confirmWebhook("https://portal-internal.gsenglish.org/api/v1/payment/webhook");
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
