package com.vendo.product_service.application.category.validation.type.validator;

import com.vendo.product_service.application.category.validation.type.TypeValidator;
import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildTypeValidator implements TypeValidator {

    private final CategoryQueryPort categoryQueryPort;
    private final AttributeQueryPort attributeQueryPort;

    @Override
    public void validate(Category category) {
        Category parent = categoryQueryPort.findById(category.getParentId(), "Parent category not found.");
        if (parent.getType() == CategoryType.CHILD) {
            throw new CategoryTypeException("A child category cannot have another child category as its parent.");
        }

        attributeQueryPort.findAllByIdsOrThrow(category.getAttributes());
    }

    @Override
    public CategoryType getType() {
        return CategoryType.CHILD;
    }

}
