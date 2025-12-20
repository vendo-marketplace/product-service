package com.vendo.product_service.service;

import com.vendo.product_service.common.mapper.CategoryMapper;
import com.vendo.product_service.db.command.CategoryCommandService;
import com.vendo.product_service.db.query.CategoryQueryService;
import com.vendo.product_service.db.model.Category;
import com.vendo.product_service.validation.factory.CreateCategoryValidationFactory;
import com.vendo.product_service.validation.strategy.CreateCategoryValidationStrategy;
import com.vendo.product_service.web.dto.CategoryResponse;
import com.vendo.product_service.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryQueryService categoryQueryService;

    private final CategoryCommandService categoryCommandService;

    private final CreateCategoryValidationFactory createCategoryValidationFactory;

    public void save(CreateCategoryRequest createCategoryRequest) {
        categoryQueryService.throwIfExistsByTitle(createCategoryRequest.title());

        CreateCategoryValidationStrategy createCategoryValidationStrategy = createCategoryValidationFactory.getValidator(createCategoryRequest.categoryType());
        createCategoryValidationStrategy.validate(createCategoryRequest);

        categoryCommandService.save(categoryMapper.toCategoryFromCategoryRequest(createCategoryRequest));
    }

    public CategoryResponse findById(String id) {
        Category category = categoryQueryService.findByIdOrThrow(id);
        return categoryMapper.toCategoryResponseFromCategory(category);
    }
}
