package com.hyudequeue.genglish.tuition_fee_manager.entities;


import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.SupportLevelEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false)
    private Integer professionalLevel;

    @Column(nullable = false)
    private Integer materialSuitability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SupportLevelEnum supportLevel;

    @Column(length = 500)
    private String extraHelpNeeded;

    private Integer overallExperience;

    @CreationTimestamp
    private LocalDateTime createdAt;
}