package com.vendo.product_service.domain.category.validation.type;

import com.vendo.product_service.domain.category.common.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CreateRootCategoryValidationStrategy implements CreateCategoryValidationStrategy {

    @Override
    public void validate(CreateCategoryRequest createCategoryRequest) {
        if (!StringUtils.isEmpty(createCategoryRequest.parentId())) {
            throw new CategoryValidationException("Root category cannot have parent id.");
        }

        if (createCategoryRequest.attributes() != null) {
            throw new CategoryValidationException("Root category cannot have attributes.");
        }
    }

    @Override
    public CategoryType getType() {
        return CategoryType.ROOT;
    }
}
