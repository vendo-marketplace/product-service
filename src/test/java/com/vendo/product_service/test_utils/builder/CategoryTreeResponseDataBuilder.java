package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;

import java.util.List;

public record CategoryTreeResponseDataBuilder() {

    public static CategoryTreeResponse from(Category response, List<Attribute> attributes) {
        return CategoryTreeResponse.builder()
                .id(response.getId())
                .title(response.getTitle())
                .code(response.getCode())
                .attributes(attributes)
                .path(response.getPath())
                .build();
    }

}
