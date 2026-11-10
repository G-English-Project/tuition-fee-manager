package com.hyudequeue.genglish.tuition_fee_manager.entities;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_name", nullable = true, length = 255)
    private String userName;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classes classes;

    @Column(nullable = true)
    private Integer month;

    /**
     * Billing year – the calendar year the invoice belongs to.
     * Stored explicitly to avoid ambiguity when the due date crosses a year boundary
     * (e.g. billing month = December, due date = January next year).
     * Derivation rule (fallback when not stored): if MONTH(dueDate) < billingMonth
     * then billingYear = YEAR(dueDate) - 1, else billingYear = YEAR(dueDate).
     */
    @Column(nullable = true)
    private Integer year;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatusEnum status;

    @Column(nullable = false)
    private Integer totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<InvoiceItem> items;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "invoice_category_map",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<InvoiceCategory> categories;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime paidAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Payment> payments;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'BANKING'")
    @Builder.Default
    private PaymentMethodEnum paymentType = PaymentMethodEnum.BANKING;
    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (paymentType == null) {
            paymentType = PaymentMethodEnum.BANKING;
        }
        if (this.year == null) {
            this.year = inferBillingYear();
        }
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.year == null) {
            this.year = inferBillingYear();
        }
    }

    public Integer getYear() {
        if (this.year != null) {
            return this.year;
        }
        return inferBillingYear();
    }

    private Integer inferBillingYear() {
        if (this.dueDate != null) {
            if (this.month != null && this.dueDate.getMonthValue() < this.month) {
                return this.dueDate.getYear() - 1;
            }
            return this.dueDate.getYear();
        }
        return this.createdAt != null ? this.createdAt.getYear() : null;
    }

    @Transient
    public String getInvoiceContent() {

        // Có item → chỉ hiển thị item
        if (items != null && !items.isEmpty()) {
            return items.stream()
                    .map(InvoiceItem::getFeeName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
        }

        // Không có item → fallback summary
        String className = classes != null ? classes.getClassName() : "Unknown class";
        Integer billingYear = getYear();
        String monthInfo = month != null 
                ? (billingYear != null ? "tháng " + month + "/" + billingYear : "tháng " + month)
                : "";

        return "Học phí " + className + " " + monthInfo;
    }
    
}
