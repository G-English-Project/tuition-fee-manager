package com.hyudequeue.genglish.tuition_fee_manager.entities;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.AttendanceEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.HomeworkEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ParticipationEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Report",
        indexes = {
                @Index(name = "idx_reports_student", columnList = "student_id"),
                @Index(name = "idx_reports_class", columnList = "class_id"),
                @Index(name = "idx_reports_created_at", columnList = "createdAt")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private Classes classRoom;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceEnum attendance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HomeworkEnum homework;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipationEnum participation;

    @Column(nullable = false)
    private Integer skillProgress;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String areasForImprovement;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String recommendedAction;

    @OneToOne(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ReportImage image;

    @Column private Boolean hasImage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        }
    }
}
