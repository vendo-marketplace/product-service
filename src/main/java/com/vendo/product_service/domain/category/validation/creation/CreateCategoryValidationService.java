package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.validation.CategoryTypeResolver;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryValidationService {

    private final CategoryTypeResolver categoryTypeResolver;

    private final CategoryCreationHandlerFactory creationHandlerFactory;

    public void validateCreation(CreateCategoryRequest createCategoryRequest) {
        CategoryType categoryType = categoryTypeResolver.resolve(createCategoryRequest.parentId(), createCategoryRequest.attributes());
        CategoryCreationHandler creationHandler = creationHandlerFactory.getHandler(categoryType);
        creationHandler.handle(createCategoryRequest);
    }

}
