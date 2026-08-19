package com.vendo.product_service.adapter.category.in.controller;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.out.mapper.DtoCategoryMapper;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.usecase.CategoryQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/categories")
public class InternalCategoryController {

    private final CategoryQueryUseCase categoryQueryUseCase;
    private final DtoCategoryMapper mapper;

    @GetMapping("/{id}")
    ResponseEntity<CategoryResponse> find(@PathVariable String id) {
        Category category = categoryQueryUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(category));
    }

}
