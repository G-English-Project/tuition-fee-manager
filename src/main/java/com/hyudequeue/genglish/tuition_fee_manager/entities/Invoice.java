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
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
        String monthInfo = month != null ? "tháng " + month : "";

        return "Học phí " + className + " " + monthInfo;
    }
    
}
