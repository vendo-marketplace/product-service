package com.vendo.product_service.adapter.category.in.controller;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.category.out.mapper.DtoCategoryMapper;
import com.vendo.product_service.application.category.CategoryService;
import com.vendo.product_service.domain.category.model.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@PreAuthorize("hasAuthority('ADMIN') and @userSecurity.checkAuthCompleted(authentication)")
public class CategoryController {

    private final CategoryService categoryService;
    private final DtoCategoryMapper categoryMapper;

    @PostMapping
    public void save(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        Category category = categoryMapper.toCategory(createCategoryRequest);
        categoryService.save(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable String id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }

}
