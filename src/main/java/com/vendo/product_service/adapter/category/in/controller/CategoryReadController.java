package com.vendo.product_service.adapter.category.in.controller;

import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.adapter.category.out.mapper.CategoryTreeMapper;
import com.vendo.product_service.application.category.CategoryReadService;
import com.vendo.product_service.domain.category.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryReadController {

    private final CategoryReadService categoryReadService;
    private final CategoryTreeMapper categoryTreeMapper;

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeResponse>> tree() {
        List<CategoryTreeResponse> getTreeList = categoryReadService.getTree()
                .stream()
                .map(categoryTreeMapper::toResponse)
                .toList();

        return ResponseEntity.ok(getTreeList);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<CategoryTreeResponse>> children(@PathVariable String id) {
        List<CategoryTreeResponse> getByChildren = categoryReadService.getChildren(id)
                .stream()
                .map(categoryTreeMapper::toResponse)
                .toList();

        return ResponseEntity.ok(getByChildren);
    }

    @GetMapping("/{id}/breadcrumbs")
    public ResponseEntity<List<CategoryTreeResponse>> breadcrumbs(@PathVariable String id) {
        List<CategoryTreeResponse> getByBreadCrumbs = categoryReadService.getBreadcrumbs(id)
                .stream()
                .map(categoryTreeMapper::toResponse)
                .toList();

        return ResponseEntity.ok(getByBreadCrumbs);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryTreeResponse>> byType(@PathVariable CategoryType type) {
        List<CategoryTreeResponse> getByType = categoryReadService.getByType(type)
                .stream()
                .map(categoryTreeMapper::toResponse)
                .toList();

        return ResponseEntity.ok(getByType);
    }
}