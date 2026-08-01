package com.hyudequeue.genglish.tuition_fee_manager.entities;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ActionTypeEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ResourceTypeEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "action_logs",
        indexes = {
                @Index(name = "idx_action_logs_created_at", columnList = "created_at"),
                @Index(name = "idx_action_logs_actor_id", columnList = "actor_id"),
                @Index(name = "idx_action_logs_resource", columnList = "resource_type, resource_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "actor_email", length = 100)
    private String actorEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_role", length = 20)
    private RoleEnum actorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    private ActionTypeEnum action;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 40)
    private ResourceTypeEnum resourceType;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "resource_label", length = 255)
    private String resourceLabel;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
