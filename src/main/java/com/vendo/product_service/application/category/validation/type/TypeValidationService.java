package com.vendo.product_service.application.category.validation.type;

import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.in.category.TypeValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TypeValidationService implements TypeValidationPort {

    private final List<TypeValidator> typeValidators;

    @Override
    public void validate(Category category) {
        TypeValidator handler = getHandler(category.getType());
        handler.validate(category);
    }

    private TypeValidator getHandler(CategoryType categoryType) {
        return typeValidators.stream()
                .filter(handler -> handler.getType() == categoryType)
                .findFirst()
                .orElseThrow(() -> new CategoryTypeException("No category creation handler."));
    }
}
