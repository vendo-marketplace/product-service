package com.vendo.product_service.service;

import com.vendo.product_service.common.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.common.mapper.CategoryMapper;
import com.vendo.product_service.db.command.CategoryCommandService;
import com.vendo.product_service.db.query.CategoryQueryService;
import com.vendo.product_service.db.model.Category;
import com.vendo.product_service.validation.factory.CategoryValidationFactory;
import com.vendo.product_service.validation.strategy.CategoryValidationStrategy;
import com.vendo.product_service.web.dto.CategoryResponse;
import com.vendo.product_service.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryQueryService categoryQueryService;

    private final CategoryCommandService categoryCommandService;

    private final CategoryValidationFactory categoryValidationFactory;

    public void save(CreateCategoryRequest createCategoryRequest) {
        Optional<Category> optionalCategory = categoryQueryService.findByTitle(createCategoryRequest.title());
        if (optionalCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Category already exists.");
        }

        CategoryValidationStrategy categoryValidationStrategy = categoryValidationFactory.getValidator(createCategoryRequest.categoryType());
        categoryValidationStrategy.validate(createCategoryRequest);

        categoryCommandService.save(categoryMapper.toCategoryFromCategoryRequest(createCategoryRequest));
    }

    public CategoryResponse findById(String id) {
        Category category = categoryQueryService.findByIdOrThrow(id);
        return categoryMapper.toCategoryResponseFromCategory(category);
    }
}
