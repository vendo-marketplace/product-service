package com.vendo.product_service.domain.category.web;

import com.vendo.product_service.domain.category.service.CategoryService;
import com.vendo.product_service.domain.category.web.dto.CategoriesResponse;
import com.vendo.product_service.domain.category.web.dto.CategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public void save(@RequestBody CategoryRequest categoryRequest) {
        categoryService.save(categoryRequest);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoriesResponse> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

}
