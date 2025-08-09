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

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethodEnum method;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}