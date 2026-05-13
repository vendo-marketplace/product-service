package com.vendo.product_service.adapter.category.in.controller;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.category.out.mapper.CategoryTreeMapper;
import com.vendo.product_service.adapter.category.out.mapper.DtoCategoryMapper;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.in.category.CategoryCommandUseCase;
import com.vendo.product_service.port.in.category.CategoryQueryUseCase;
import com.vendo.product_service.port.in.category.TypeValidationPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@PreAuthorize("@userSecurity.validateActivatedAdmin(authentication)")
class CategoryController {

    private final CategoryCommandUseCase categoryUseCase;
    private final CategoryQueryUseCase categoryQueryUseCase;
    private final CategoryTreeMapper categoryTreeMapper;
    private final DtoCategoryMapper categoryMapper;
    private final TypeValidationPort typeValidationPort;

    @PostMapping
    void save(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = categoryMapper.toCategory(request);
        typeValidationPort.validate(category);
        categoryUseCase.save(category);
    }

    @GetMapping("/{id}")
    ResponseEntity<CategoryResponse> find(@PathVariable String id) {
        Category category = categoryQueryUseCase.findById(id);
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }

    @GetMapping("/tree")
    ResponseEntity<List<CategoryTreeResponse>> tree() {
        return ResponseEntity.ok(categoryTreeMapper.toResponse(categoryQueryUseCase.getTree()));
    }

}
