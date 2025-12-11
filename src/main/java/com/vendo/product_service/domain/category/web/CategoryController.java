package com.vendo.product_service.domain.category.web;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.service.CategoryService;
import com.vendo.product_service.domain.category.web.dto.CategoriesResponse;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@PreAuthorize("hasAuthority('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public void save(@RequestBody CreateCategoryRequest createCategoryRequest) {
        categoryService.save(createCategoryRequest);
    }

    @GetMapping("/all")
    public ResponseEntity<CategoriesResponse> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping
    public ResponseEntity<CategoriesResponse> findByType(@RequestParam CategoryType type) {
        return ResponseEntity.ok(categoryService.findAllByType(type));
    }

}
