package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceCategoryResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.CategoryStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceCategoryService {

    InvoiceCategoryResponseDTO create(String name, String colorHex);

    InvoiceCategoryResponseDTO update(Long categoryId, String name, String colorHex);
    void softDelete(Long categoryId);
    InvoiceCategoryResponseDTO getById(Long categoryId);

    public List<InvoiceCategoryResponseDTO> list();
}
