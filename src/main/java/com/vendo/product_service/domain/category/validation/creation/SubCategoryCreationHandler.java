package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.common.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.validation.CategoryTypeResolver;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubCategoryCreationHandler implements CategoryCreationHandler {

    private final CategoryQueryService categoryQueryService;

    private final CategoryTypeResolver categoryTypeResolver;

    @Override
    public void handle(CreateCategoryRequest createCategoryRequest) {
        categoryQueryService.throwExistsByCode(createCategoryRequest.code());

        Category parentCategory = categoryQueryService.findByParentId(createCategoryRequest.parentId());
        CategoryType parentCategoryType = categoryTypeResolver.resolve(parentCategory.getParentId(), parentCategory.getAttributes());

        if (parentCategoryType == CategoryType.CHILD) {
            throw new CategoryNotFoundException("Sub category shouldn't have child category as parent.");
        }
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.SUB;
    }
}
