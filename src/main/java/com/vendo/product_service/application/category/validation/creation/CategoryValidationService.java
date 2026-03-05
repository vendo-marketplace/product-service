package com.vendo.product_service.application.category.validation.creation;

import com.vendo.product_service.domain.category.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryValidationService {

    private final CreationValidationFactory creationHandlerFactory;

    public void validateCreation(Category category) {
        CreationValidator creationHandler = creationHandlerFactory.getHandler(category.getType());
        creationHandler.validate(category.getParentId());
    }
}
