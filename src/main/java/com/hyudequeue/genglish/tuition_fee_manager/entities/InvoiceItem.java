package com.hyudequeue.genglish.tuition_fee_manager.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Invoice_Items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private String feeName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double amount;
}