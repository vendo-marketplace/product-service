package com.vendo.product_service.application.category.validation.creation;

import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreationValidationFactory {

    private final List<CreationValidator> creationValidators;

    public CreationValidator getHandler(CategoryType categoryType) {
        return creationValidators.stream()
                .filter(handler -> handler.getType() == categoryType)
                .findFirst()
                .orElseThrow(() -> new CategoryTypeException("No category creation handler."));
    }

}
