package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassCategoryRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassCategoryResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassCategoryRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ClassCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassCategoryServiceImpl implements ClassCategoryService {

    private final ClassCategoryRepository classCategoryRepository;

    @Override
    public ClassCategoryResponseDto CreateCategory(ClassCategoryRequestDto dto) {
        ClassCategory category = classCategoryRepository.save(dto.toEntity());
        return ClassCategoryResponseDto.fromEntity(category);
    }

    @Override
    public List<ClassCategoryResponseDto> GetAllCategories() {
        return classCategoryRepository.findAll()
                .stream()
                .map(ClassCategoryResponseDto::fromEntity)
                .toList();
    }

    @Override
    public ClassCategoryResponseDto UpdateCategory(Long id, ClassCategoryRequestDto dto) {
        ClassCategory category = classCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(dto.getName());
        category.setColor(dto.getColor());
        category.setStatus(dto.getStatus());

        return ClassCategoryResponseDto.fromEntity(classCategoryRepository.save(category));
    }

    @Override
    public void DeleteCategory(Long id) {
        if (!classCategoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        classCategoryRepository.deleteById(id);
    }
    @Override
    public ClassCategoryResponseDto GetCategoryById(Long id) {
        ClassCategory category = classCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return ClassCategoryResponseDto.fromEntity(category);
    }

}
