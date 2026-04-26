package com.vendo.product_service.application.category.validation.type;

import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TypeValidationFactory {

    private final List<TypeValidator> typeValidators;

    public TypeValidator getHandler(CategoryType categoryType) {
        return typeValidators.stream()
                .filter(handler -> handler.getType() == categoryType)
                .findFirst()
                .orElseThrow(() -> new CategoryTypeException("No category creation handler."));
    }

}
