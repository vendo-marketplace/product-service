package com.vendo.product_service.adapter.in.category.controller;

import com.vendo.product_service.adapter.in.category.dto.CategoryEntityResponse;
import com.vendo.product_service.adapter.in.category.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.in.category.mapper.CategoryDtoMapper;
import com.vendo.product_service.application.CategoryUseCase;
import com.vendo.product_service.domain.category.model.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@PreAuthorize("hasAuthority('ADMIN')")
public class CategoryController {

    private final CategoryUseCase categoryUseCase;
    private final CategoryDtoMapper categoryDtoMapper;

    @PostMapping
    public void save(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        Category category= categoryDtoMapper.toCategoryDomainFromCategoryRequest(createCategoryRequest);
        categoryUseCase.save(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryEntityResponse> findById(@PathVariable String id) {
        Category category = categoryUseCase.findById(id);
        CategoryEntityResponse categoryEntityResponse = categoryDtoMapper.toCategoryEntityResponseFromCategory(category);
        return ResponseEntity.ok(categoryEntityResponse);
    }

}
