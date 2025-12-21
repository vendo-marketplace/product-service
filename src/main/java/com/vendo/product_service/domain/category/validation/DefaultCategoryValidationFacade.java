package com.vendo.product_service.domain.category.validation;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.validation.attribute.CategoryAttributeValidator;
import com.vendo.product_service.domain.category.validation.type.CategoryTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultCategoryValidationFacade implements CategoryValidationFacade {

    private final CategoryTypeValidator categoryTypeValidator;

    private final CategoryAttributeValidator categoryAttributeValidator;

    @Override
    public void validateCategoryOnSave(String categoryId,  Map<String, List<String>> attributes) {
        categoryTypeValidator.validateCategoryType(categoryId, CategoryType.CHILD);
        categoryAttributeValidator.validateCategoryAttributes(categoryId, attributes);
    }

}
