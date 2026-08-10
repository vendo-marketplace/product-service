package com.vendo.product_service.adapter.category.in.controller;

import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.category.in.dto.UpdateCategoryRequest;
import com.vendo.product_service.adapter.category.out.mapper.DtoCategoryMapper;
import com.vendo.product_service.adapter.image.out.mapper.ImageMapper;
import com.vendo.product_service.infrastructure.shared.annotation.ImageFile;
import com.vendo.product_service.port.category.usecase.CategoryCommandUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryCommandController {

    private final CategoryCommandUseCase categoryUseCase;
    private final DtoCategoryMapper categoryMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    void save(@Valid @RequestBody CreateCategoryRequest request) {
        categoryUseCase.save(categoryMapper.toCategory(request));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    void update(@RequestParam String id, @Valid @RequestBody UpdateCategoryRequest request) {
        categoryUseCase.update(id, categoryMapper.toCategory(request));
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    void uploadImage(
            @RequestParam String id,
            @Valid @ImageFile @RequestParam MultipartFile image) {
        categoryUseCase.uploadImage(id, ImageMapper.toImage(image));
    }

    @DeleteMapping("/image")
    @PreAuthorize("hasAuthority('ADMIN')")
    void removeImage(@RequestParam String id) {
        categoryUseCase.removeImage(id);
    }
}
