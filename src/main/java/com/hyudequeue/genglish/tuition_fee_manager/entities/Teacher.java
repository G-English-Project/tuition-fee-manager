package com.hyudequeue.genglish.tuition_fee_manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = true, length = 255)
    private String address;

    @Column(nullable = true, length = 500)
    private String image;

    @Column(nullable = false, columnDefinition = "DECIMAL(3,2) DEFAULT 0.0")
    private Double rating;

    @Column(nullable = true, length = 500)
    private String experience;

    @ElementCollection
    @CollectionTable(name = "teacher_specialties", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "specialty")
    private List<String> specialties;

    @ElementCollection
    @CollectionTable(name = "teacher_credentials", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "credential")
    private List<String> credentials;

    @ElementCollection
    @CollectionTable(name = "teacher_languages", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "language")
    private List<String> languages;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String bio;

    @ElementCollection
    @CollectionTable(name = "teacher_achievements", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "achievement")
    private List<String> achievements;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
