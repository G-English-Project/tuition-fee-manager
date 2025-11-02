package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.CategoryStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassCategoryRequestDto {
    @NotBlank(message = "Code cannot be blank")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "Color must be valid hex format, e.g. #FFFFFF or #FFF")
    private String color;

    @NotBlank
    private String name;

    private CategoryStatusEnum status = CategoryStatusEnum.ACTIVE;

    public ClassCategory toEntity() {
        return ClassCategory.builder()
                .color(this.color)
                .name(this.name)
                .status(this.status)
                .build();
    }
}