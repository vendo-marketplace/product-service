package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.model.CategoryType;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.validation.CategoryTypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryValidationService {

    private final CategoryTypeResolver categoryTypeResolver;

    private final CategoryCreationValidationFactory creationHandlerFactory;

    public void validateCreation(Category category) {
        CategoryType categoryType = categoryTypeResolver.resolve(category.getParentId(), category.getAttributes());
        CategoryCreationValidator creationHandler = creationHandlerFactory.getHandler(categoryType);
        creationHandler.validate(category.getParentId());
    }

}
