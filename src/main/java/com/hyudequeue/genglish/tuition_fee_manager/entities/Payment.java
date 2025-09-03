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

    /**
     * Mặc định: "<Tên> | <Tên lớp> | <Mã hóa đơn/ID>"
     * Nếu dài > 24 ký tự thì bỏ tên lớp => "<Tên> | <Mã hóa đơn/ID>"
     */
    private static String buildDefaultDescription(Invoice invoice) {
        String nameSource = (invoice.getUserName() != null && !invoice.getUserName().isBlank())
                ? invoice.getUserName()
                : (invoice.getUser() != null ? invoice.getUser().getFullName() : null);
        String givenName = onlyGivenName(nameSource);

        String className = (invoice.getClasses() != null && invoice.getClasses().getClassName() != null)
                ? invoice.getClasses().getClassName().trim()
                : "LOP";

        String code = String.valueOf(invoice.getInvoiceId());

        String fullDesc = String.format("%s | %s | %s", givenName, className, code);

        if (fullDesc.length() > 24) {
            fullDesc = String.format("%s | %s", givenName, code);
        }

        return fullDesc;
    }

    public static Payment fromCreateRequest(CreatePaymentRequest req, Invoice invoice, PayOSProperties properties) {
        String fullName = invoice.getUser() != null ? invoice.getUser().getFullName() : "Hoc vien";
        String[] parts = fullName.trim().split("\\s+");

        // Lấy 2 từ cuối nếu tên có nhiều hơn 1 từ, ngược lại lấy 1 từ
        String shortName;
        if (parts.length >= 2) {
            shortName = parts[parts.length - 2] + " " + parts[parts.length - 1];
        } else {
            shortName = parts[0];
        }

        String shownId = GenerateId.formatId(invoice.getInvoiceId());

        // Lấy tên lớp (nếu có)
        String className = (invoice.getClasses() != null && invoice.getClasses().getClassName() != null)
                ? invoice.getClasses().getClassName().trim()
                : "LOP";

        // Format description: "<Tên ngắn> | <Tên lớp> | <Mã hóa đơn>"
        String desc = String.format("%s | %s | %s", shortName, className, shownId);

        return Payment.builder()
                .invoice(invoice)
                .amount(invoice.getTotalAmount())
                .currency("VND")
                .description(desc)   // 🚀 luôn do BE build
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
