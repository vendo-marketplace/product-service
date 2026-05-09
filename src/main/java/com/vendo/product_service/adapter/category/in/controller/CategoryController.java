package com.vendo.product_service.adapter.category.in.controller;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.category.out.mapper.CategoryTreeMapper;
import com.vendo.product_service.adapter.category.out.mapper.DtoCategoryMapper;
import com.vendo.product_service.application.category.CategoryReadService;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.in.category.CategoryUseCase;
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

    private final CategoryUseCase categoryUseCase;
    private final TypeValidationPort typeValidationPort;
    private final DtoCategoryMapper mapper;
    private final CategoryTreeMapper categoryTreeMapper;
    private final CategoryReadService categoryReadService;

    @PostMapping
    void save(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = mapper.toCategory(request);
        typeValidationPort.validate(category);
        categoryUseCase.save(category);
    }

    @GetMapping("/{id}")
    ResponseEntity<CategoryResponse> find(@PathVariable String id) {
        Category category = categoryUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(category));
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeResponse>> tree() {
        return ResponseEntity.ok(categoryTreeMapper.toResponse(categoryReadService.getTree()));
    }

}
