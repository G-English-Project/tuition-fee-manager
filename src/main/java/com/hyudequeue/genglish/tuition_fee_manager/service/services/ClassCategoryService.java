package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassCategoryResponseDto;

import java.util.List;

public interface ClassCategoryService {

    ClassCategoryResponseDto create(String name, String colorHex);

    ClassCategoryResponseDto update(Long categoryId, String name, String colorHex);

    void softDelete(Long categoryId);

    ClassCategoryResponseDto getById(Long categoryId);

    List<ClassCategoryResponseDto> list();
}