package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.common.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryCreationHandlerFactory {

    private final List<CategoryCreationHandler> categoryCreationHandlers;

    public CategoryCreationHandler getHandler(CategoryType categoryType) {
        return categoryCreationHandlers.stream()
                .filter(handler -> handler.getCategoryType() == categoryType)
                .findFirst()
                .orElseThrow(() -> new CategoryTypeException("No category creation handler."));
    }

}
