package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceCategoryResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.CategoryStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.InvoiceCategory;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceCategoryRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceCategoryService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InvoiceCategoryServiceImpl implements InvoiceCategoryService {

    private final InvoiceCategoryRepository repository;

    public InvoiceCategoryServiceImpl(InvoiceCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public InvoiceCategoryResponseDTO create(String name, String colorHex) {
        if (repository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }
        validateHex(colorHex);

        InvoiceCategory entity = InvoiceCategory.builder()
                .name(name)
                .color(colorHex)
                .status(CategoryStatusEnum.ACTIVE)
                .build();

        return InvoiceCategoryResponseDTO.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public InvoiceCategoryResponseDTO update(Long categoryId, String name, String colorHex) {
        InvoiceCategory entity = repository.findById(categoryId)
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

        return InvoiceCategoryResponseDTO.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void softDelete(Long categoryId) {
        InvoiceCategory entity = repository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (entity.getStatus() == CategoryStatusEnum.INACTIVE) return; // idempotent
        entity.setStatus(CategoryStatusEnum.INACTIVE);
        repository.save(entity);
    }

    @Override
    public InvoiceCategoryResponseDTO getById(Long categoryId) {
        InvoiceCategory entity = repository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        return InvoiceCategoryResponseDTO.toDto(entity);
    }

    @Override
    public List<InvoiceCategoryResponseDTO> list() {
        return repository.findAllByStatus(CategoryStatusEnum.ACTIVE)
                .stream()
                .map(InvoiceCategoryResponseDTO::toDto)
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
