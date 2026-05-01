package com.vendo.product_service.adapter.category.in.controller;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.category.out.mapper.DtoCategoryMapper;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.in.category.CategoryUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@PreAuthorize("@userSecurity.validateActivatedAdmin(authentication)")
class CategoryController {

    private final DtoCategoryMapper mapper;
    private final CategoryUseCase categoryUseCase;

    @PostMapping
    void save(@Valid @RequestBody CreateCategoryRequest request) {
        categoryUseCase.save(mapper.toCategory(request));
    }

    @GetMapping("/{id}")
    ResponseEntity<CategoryResponse> find(@PathVariable String id) {
        Category category = categoryUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(category));
    }

}
