package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentCategoryCreationHandler implements CategoryCreationHandler {

    @Override
    public void handle(CreateCategoryRequest createCategoryRequest) {
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.PARENT;
    }

}
