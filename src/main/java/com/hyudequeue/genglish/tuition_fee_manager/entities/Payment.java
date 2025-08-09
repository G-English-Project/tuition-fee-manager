package com.hyudequeue.genglish.tuition_fee_manager.entities;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
}