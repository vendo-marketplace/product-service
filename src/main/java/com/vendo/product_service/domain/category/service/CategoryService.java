package com.vendo.product_service.domain.category.service;

import com.vendo.product_service.domain.category.common.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.common.mapper.CategoryMapper;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.command.CategoryCommandService;
import com.vendo.product_service.domain.category.db.query.CategoryQueryService;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.validation.factory.CategoryValidationFactory;
import com.vendo.product_service.domain.category.validation.strategy.CategoryValidationStrategy;
import com.vendo.product_service.domain.category.web.dto.CategoriesResponse;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public CategoriesResponse findAll() {
        List<Category> categories = categoryQueryService.findAll();
        return categoryMapper.toCategoriesResponseFromCategories(categories);
    }

    public CategoryResponse findById(String id) {
        Category category = categoryQueryService.findById(id);
        return categoryMapper.toCategoryResponseFromCategory(category);
    }

    public CategoriesResponse findAllByType(CategoryType categoryType) {
        List<Category> categories = categoryQueryService.findAllByType(categoryType);
        return categoryMapper.toCategoriesResponseFromCategories(categories);
    }
}
