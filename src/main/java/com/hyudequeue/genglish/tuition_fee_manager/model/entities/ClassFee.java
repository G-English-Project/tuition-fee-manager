package com.hyudequeue.genglish.tuition_fee_manager.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Class_Fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classFeeId;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classes classes;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}