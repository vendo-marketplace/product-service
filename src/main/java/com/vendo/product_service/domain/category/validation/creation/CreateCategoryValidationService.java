package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryValidationService {

    private final CategoryCreationValidationFactory creationHandlerFactory;

    public void validateCreation(Category category) {
        CategoryCreationValidator creationHandler = creationHandlerFactory.getHandler(category.getType());
        creationHandler.validate(category.getParentId());
    }

}
