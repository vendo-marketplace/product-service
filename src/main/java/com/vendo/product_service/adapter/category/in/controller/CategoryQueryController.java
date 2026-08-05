package com.vendo.product_service.adapter.category.in.controller;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.adapter.category.out.mapper.CategoryTreeMapper;
import com.vendo.product_service.adapter.category.out.mapper.DtoCategoryMapper;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.usecase.CategoryQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
class CategoryQueryController {

    private final CategoryQueryUseCase categoryQueryUseCase;
    private final CategoryTreeMapper categoryTreeMapper;
    private final DtoCategoryMapper categoryMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<CategoryResponse> find(@PathVariable String id) {
        Category category = categoryQueryUseCase.findById(id);
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }

    @GetMapping("/tree")
    ResponseEntity<CategoryTreeResponse> tree() {
        List<CategoryTreeResponse.CategoryTree> tree = categoryTreeMapper.toResponse(categoryQueryUseCase.getTree());
        return ResponseEntity.ok(CategoryTreeResponse.builder().data(tree).build());
    }

}
