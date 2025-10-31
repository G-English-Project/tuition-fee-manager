package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

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
    public ResponseEntity<List<ClassCategoryResponseDto>> list() {
        return ResponseEntity.ok(classCategoryService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassCategoryResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(classCategoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ClassCategoryResponseDto> create(
            @RequestParam String name,
            @RequestParam String color
    ) {
        return ResponseEntity.ok(classCategoryService.create(name, color));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassCategoryResponseDto> update(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color
    ) {
        return ResponseEntity.ok(classCategoryService.update(id, name, color));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        classCategoryService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}