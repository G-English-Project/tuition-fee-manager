package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassCategoryResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.CategoryStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ResourceTypeEnum;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassCategoryRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ActionLogService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ClassCategoryService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.ActionLogDetail;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassCategoryServiceImpl implements ClassCategoryService {

    private final ClassCategoryRepository repository;
    private final ActionLogService actionLogService;

    @Override
    @Transactional
    public ClassCategoryResponseDto create(String name, String colorHex) {
        if (repository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }
        validateHex(colorHex);

        ClassCategory entity = ClassCategory.builder()
                .name(name)
                .color(colorHex)
                .status(CategoryStatusEnum.ACTIVE)
                .build();

        ClassCategory saved = repository.save(entity);
        actionLogService.created(
                ResourceTypeEnum.CLASS_CATEGORY,
                saved.getCategoryId(),
                saved.getName()
        );
        return ClassCategoryResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public ClassCategoryResponseDto update(Long categoryId, String name, String colorHex) {
        ClassCategory entity = repository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (name != null && !name.equalsIgnoreCase(entity.getName())) {
            if (repository.existsByNameIgnoreCase(name)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
            }
            entity.setName(name);
        }

        if (colorHex != null) {
            validateHex(colorHex);
            entity.setColor(colorHex);
        }

        ClassCategory saved = repository.save(entity);
        actionLogService.updated(
                ResourceTypeEnum.CLASS_CATEGORY,
                saved.getCategoryId(),
                saved.getName()
        );
        return ClassCategoryResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void softDelete(Long categoryId) {
        ClassCategory entity = repository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (entity.getStatus() == CategoryStatusEnum.INACTIVE) return;
        entity.setStatus(CategoryStatusEnum.INACTIVE);
        repository.save(entity);
        actionLogService.deleted(
                ResourceTypeEnum.CLASS_CATEGORY,
                entity.getCategoryId(),
                entity.getName(),
                ActionLogDetail.of("status", "INACTIVE")
        );
    }

    @Override
    public ClassCategoryResponseDto getById(Long categoryId) {
        ClassCategory entity = repository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        return ClassCategoryResponseDto.fromEntity(entity);
    }

    @Override
    public List<ClassCategoryResponseDto> list() {
        return repository.findAllByStatus(CategoryStatusEnum.ACTIVE)
                .stream()
                .map(ClassCategoryResponseDto::fromEntity)
                .toList();
    }

    // ===== Helpers =====
    private void validateHex(String hex) {
        if (hex == null || !hex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Color must be valid hex format, e.g. #FFFFFF or #FFF"
            );
        }
    }
}