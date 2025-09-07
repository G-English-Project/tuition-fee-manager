package com.hyudequeue.genglish.tuition_fee_manager.entities;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request.CreatePaymentRequest;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.GenerateId;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PayOSProperties;
import jakarta.persistence.*;
import lombok.*;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false) private String buyerName;
    @Column(nullable = false) private String buyerEmail;
    @Column(nullable = false) private String buyerPhone;

    @Column(nullable = false) private String cancelUrl;
    @Column(nullable = false) private String returnUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusEnum status;


    @PrePersist
    public void prePersist() {
        if (this.currency == null) this.currency = "VND";
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.description == null) this.description = "THANH TOAN HOA DON";
    }

    @Transient
    public List<InvoiceItem> getInvoiceItems() {
        return (invoice != null && invoice.getItems() != null) ? invoice.getItems() : List.of();
    }

    private static String onlyGivenName(String fullName) {
        if (fullName == null) return "HOC VIEN";
        String trimmed = fullName.trim().replaceAll("\\s+", " ");
        if (trimmed.isEmpty()) return "HOC VIEN";
        String[] parts = trimmed.split(" ");
        return parts[parts.length - 1];
    }

    // ======================= Helpers =======================

    // ======================= Helpers =======================

    private static String lastToken(String s) {
        if (s == null) return null;
        String[] parts = s.trim().split("\\s+");
        return parts.length == 0 ? null : parts[parts.length - 1];
    }

    /** Format mã hoá đơn: # + 6 chữ số (bù 0 ở đầu) */
    private static String formatInvoiceCode(Long invoiceId) {
        if (invoiceId == null) return "#000000";
        return String.format("#%06d", invoiceId);
    }

    /**
     * Build theo rule chung (không xét baseDesc):
     * Primary: "<nameLast> | <classLast> | <code>"
     * >24  ->  "<nameLast> | <code>"
     * >24  ->  "HOA DON | <code>"
     */
    private static String buildCore(String nameLast, String classLast, String code) {
        String n = (nameLast == null || nameLast.isBlank()) ? "HV" : nameLast;
        String c = (classLast == null || classLast.isBlank()) ? "LOP" : classLast;

        String primary = String.format("%s | %s | %s", n, c, code);
        if (primary.length() <= 24) return primary;

        String noClass = String.format("%s | %s", n, code);
        if (noClass.length() <= 24) return noClass;

        return String.format("HOA DON | %s", code);
    }

    /**
     * Nếu có baseDesc:
     *  - Thử "<baseDesc> | <code>"
     *  - Nếu <=24: dùng luôn
     *  - Nếu >24: bỏ baseDesc và quay về buildCore (Name/Class rules)
     * Nếu không có baseDesc: dùng buildCore.
     */
    private static String buildDescriptionWithRules(
            String nameLast, String classLast, String baseDescOrNull, String code) {

        if (baseDescOrNull != null && !baseDescOrNull.isBlank()) {
            String candidate = String.format("%s | %s", baseDescOrNull.trim(), code);
            if (candidate.length() <= 24) {
                return candidate;
            }
            // >24: bỏ baseDesc và dùng rule chuẩn
            return buildCore(nameLast, classLast, code);
        }

        return buildCore(nameLast, classLast, code);
    }

// ======================= Default description (có thể tái dùng) =======================

    private static String buildDefaultDescription(Invoice invoice, String baseDescOrNull) {
        // Lấy tên cuối từ fullName; fallback userName; rồi "HV"
        String fullName = (invoice.getUser() != null) ? invoice.getUser().getFullName() : null;
        String nameLast = lastToken(fullName);
        if (nameLast == null) {
            String nameSource = (invoice.getUserName() != null && !invoice.getUserName().isBlank())
                    ? invoice.getUserName()
                    : null;
            nameLast = lastToken(nameSource);
        }
        if (nameLast == null || nameLast.isBlank()) nameLast = "HV";

        // Lấy class cuối
        String classNameRaw = (invoice.getClasses() != null && invoice.getClasses().getClassName() != null)
                ? invoice.getClasses().getClassName().trim()
                : "LOP";
        String classLast = lastToken(classNameRaw);
        if (classLast == null || classLast.isBlank()) classLast = "LOP";

        // Mã hoá đơn (# + 6 số)
        String code = formatInvoiceCode(invoice.getInvoiceId());

        return buildDescriptionWithRules(nameLast, classLast, baseDescOrNull, code);
    }

// ======================= fromCreateRequest =======================

    public static Payment fromCreateRequest(CreatePaymentRequest req, Invoice invoice, PayOSProperties properties) {
        // nameLast: từ cuối của fullName
        String fullName = (invoice.getUser() != null) ? invoice.getUser().getFullName() : "Hoc vien";
        String nameLast = lastToken(fullName);
        if (nameLast == null || nameLast.isBlank()) nameLast = "HV";

        String classNameRaw = (invoice.getClasses() != null && invoice.getClasses().getClassName() != null)
                ? invoice.getClasses().getClassName().trim()
                : "LOP";
        String classLast = lastToken(classNameRaw);
        if (classLast == null || classLast.isBlank()) classLast = "LOP";

        String code = formatInvoiceCode(invoice.getInvoiceId());

        String baseDesc = (req.getDescription() != null && !req.getDescription().isBlank())
                ? req.getDescription().trim()
                : null;

        String desc = buildDescriptionWithRules(nameLast, classLast, baseDesc, code);

        return Payment.builder()
                .invoice(invoice)
                .amount(invoice.getTotalAmount())
                .currency("VND")
                .description(desc) 
                .buyerName(req.getBuyerName())
                .buyerEmail(req.getBuyerEmail())
                .buyerPhone(req.getBuyerPhone())
                .cancelUrl(properties.getCancelUrl())
                .returnUrl(properties.getReturnUrl())
                .status(PaymentStatusEnum.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static PaymentData toPaymentData(Payment payment, long expiredAtSeconds) {
        PaymentData.PaymentDataBuilder builder = vn.payos.type.PaymentData.builder()
                .orderCode(payment.getPaymentId())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .cancelUrl(payment.getCancelUrl())
                .returnUrl(payment.getReturnUrl())
                .buyerName(payment.getBuyerName())
                .buyerEmail(payment.getBuyerEmail())
                .buyerPhone(payment.getBuyerPhone())
                .expiredAt(expiredAtSeconds);

        for (InvoiceItem item : payment.getInvoiceItems()) {
            builder.item(
                    ItemData.builder()
                            .name(item.getFeeName())
                            .quantity(item.getQuantity())
                            .price(item.getAmount())
                            .build()
            );
        }
        return builder.build();
    }
}
