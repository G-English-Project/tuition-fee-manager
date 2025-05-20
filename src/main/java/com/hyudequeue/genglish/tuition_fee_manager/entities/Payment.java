package com.hyudequeue.genglish.tuition_fee_manager.entities;

import com.hyudequeue.genglish.tuition_fee_manager.utility.Enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Column(nullable = false)
    private Double amountPaid;

    @Column(nullable = false)
    private Integer method;

    private String transactionRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusEnum status;
}