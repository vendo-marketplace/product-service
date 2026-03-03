package com.vendo.product_service.adapter.category.in;

import com.vendo.product_service.adapter.category.in.dto.CategoryEntityResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.category.in.mapper.CategoryMapper;
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
@PreAuthorize("hasAuthority('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public void save(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        Category category= categoryMapper.toEntity(createCategoryRequest);
        categoryService.save(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryEntityResponse> findById(@PathVariable String id) {
        Category category = categoryService.findById(id);
        CategoryEntityResponse categoryEntityResponse = categoryMapper.toResponse(category);
        return ResponseEntity.ok(categoryEntityResponse);
    }

}
