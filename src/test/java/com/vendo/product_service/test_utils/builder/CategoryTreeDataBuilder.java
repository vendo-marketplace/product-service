package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;

import java.util.List;

public record CategoryTreeDataBuilder() {

    public static CategoryTreeResponse.CategoryTree from(Category category, List<Attribute> attributes) {
        CategoryTreeResponse.CategoryTree categoryTree = CategoryTreeResponse.CategoryTree.builder()
                .id(category.getId())
                .attributes(attributes)
                .path(category.getPath())
                .build();

        return CategoryTreeResponse.CategoryTree.builder()
                .id(category.getId())
                .title(category.getTitle())
                .code(category.getCode())
                .attributes(List.of())
                .path(category.getPath())
                .children(List.of(categoryTree))
                .build();
    }

}
