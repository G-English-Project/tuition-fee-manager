package com.hyudequeue.genglish.tuition_fee_manager.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ReportImages")
public class ReportImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false, unique = true)
    private Report report;

    @Lob @Column(columnDefinition = "LONGTEXT")
    private String imageBase64; // full

    @Lob @Column(columnDefinition = "LONGTEXT")
    private String imageThumbBase64; // thumb

    @Column(length=50)
    private String mimeType;
}
