package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassCategoryRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassCategoryResponseDto;

import java.util.List;

public interface ClassCategoryService {
    ClassCategoryResponseDto CreateCategory(ClassCategoryRequestDto dto);
    List<ClassCategoryResponseDto> GetAllCategories();
    ClassCategoryResponseDto UpdateCategory(Long id, ClassCategoryRequestDto dto);
    void DeleteCategory(Long id);
    ClassCategoryResponseDto GetCategoryById(Long id);

}
