package com.hyudequeue.genglish.tuition_fee_manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Class_Enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classes classes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    private LocalDateTime unenrolledAt;
}