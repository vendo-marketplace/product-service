package com.vendo.product_service.domain.category.service;

import com.vendo.product_service.domain.category.common.mapper.CategoryMapper;
import com.vendo.product_service.domain.category.db.cqrs.command.CategoryCommandService;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.validation.creation.CreateCategoryValidationService;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryQueryService categoryQueryService;

    private final CategoryCommandService categoryCommandService;

    private final CreateCategoryValidationService createCategoryValidationService;

    public void save(CreateCategoryRequest createCategoryRequest) {
        createCategoryValidationService.validateCreation(createCategoryRequest);
        categoryQueryService.throwExistsByCode(createCategoryRequest.code());
        categoryCommandService.save(categoryMapper.toCategoryFromCategoryRequest(createCategoryRequest));
    }

    public CategoryResponse findById(String id) {
        Category category = categoryQueryService.findById(id);
        return categoryMapper.toCategoryResponseFromCategory(category);
    }
}
