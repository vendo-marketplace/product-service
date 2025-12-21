package com.vendo.product_service.domain.category.service;

import com.vendo.product_service.domain.category.common.mapper.CategoryMapper;
import com.vendo.product_service.domain.category.db.cqrs.command.CategoryCommandService;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.validation.type.CreateCategoryValidationFactory;
import com.vendo.product_service.domain.category.validation.type.CreateCategoryValidationStrategy;
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
