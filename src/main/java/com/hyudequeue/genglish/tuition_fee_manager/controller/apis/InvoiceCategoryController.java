package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceCategoryResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.InvoiceCategoryEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.INVOICE_CATEGORY_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(INVOICE_CATEGORY_API)
public class InvoiceCategoryController {

    private final InvoiceCategoryService categoryService;

    @Operation(summary = "Create new invoice category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created or restored"),
            @ApiResponse(responseCode = "409", description = "Name already exists (ACTIVE)")
    })
    @PostMapping(CREATE)
    public ResponseEntity<ApiResp<InvoiceCategoryResponseDTO>> createCategory(
            @Parameter(description = "Category name") @RequestParam String name,
            @Parameter(description = "Hex color, e.g. #FFFFFF") @RequestParam String colorHex
    ) {
        return ApiResp.success(categoryService.create(name, colorHex));
    }

    @Operation(summary = "Update invoice category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Name already exists (ACTIVE)")
    })
    @PutMapping(UPDATE)
    public ResponseEntity<ApiResp<InvoiceCategoryResponseDTO>> updateCategory(
            @Parameter(description = "Category ID") @RequestParam Long categoryId,
            @Parameter(description = "New name (optional)") @RequestParam(required = false) String name,
            @Parameter(description = "New hex color (optional)") @RequestParam(required = false) String colorHex
    ) {
        return ApiResp.success(categoryService.update(categoryId, name, colorHex));
    }

    @Operation(summary = "Soft delete invoice category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Soft deleted"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping(SOFT_DELETE)
    public ResponseEntity<ApiResp<String>> softDeleteCategory(
            @Parameter(description = "Category ID") @RequestParam Long categoryId
    ) {
        categoryService.softDelete(categoryId);
        return ApiResp.success("Category soft-deleted");
    }

    @Operation(summary = "Get category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping(GET_BY_ID)
    public ResponseEntity<ApiResp<InvoiceCategoryResponseDTO>> getById(
            @Parameter(description = "Category ID") @RequestParam Long categoryId
    ) {
        return ApiResp.success(categoryService.getById(categoryId));
    }

    @Operation(summary = "Get all ACTIVE categories (paged)")
    @GetMapping(GET_ALL)
    public ResponseEntity<ApiResp<List<InvoiceCategoryResponseDTO>>> getAll() {
        return ApiResp.success(categoryService.list());
    }
}
