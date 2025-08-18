package com.hyudequeue.genglish.tuition_fee_manager.entities;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.CategoryStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(
        name = "InvoiceCategories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"code"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "Color must be valid hex format, e.g. #FFFFFF or #FFF")
    @Column(nullable = false, length = 7) // #RRGGBB có 7 ký tự
    private String color;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    @Builder.Default
    private CategoryStatusEnum status = CategoryStatusEnum.ACTIVE;

}
