package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassCategoryRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassCategoryResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ClassCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/class-categories")
@RequiredArgsConstructor
public class ClassCategoryController {

    private final ClassCategoryService classCategoryService;

    @GetMapping
    public ResponseEntity<List<ClassCategoryResponseDto>> getAll() {
        return ResponseEntity.ok(classCategoryService.GetAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassCategoryResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(classCategoryService.GetCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<ClassCategoryResponseDto> create(@RequestBody ClassCategoryRequestDto dto) {
        return ResponseEntity.ok(classCategoryService.CreateCategory(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassCategoryResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClassCategoryRequestDto dto
    ) {
        return ResponseEntity.ok(classCategoryService.UpdateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        classCategoryService.DeleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
