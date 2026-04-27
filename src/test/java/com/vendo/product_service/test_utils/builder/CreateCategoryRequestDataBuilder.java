package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;

import java.util.List;
import java.util.UUID;

public class CreateCategoryRequestDataBuilder {

    public static CreateCategoryRequest.CreateCategoryRequestBuilder withAllFields() {
        return CreateCategoryRequest.builder()
                .title("CategoryEntity title")
                .parentId("parent_id")
                .code(String.valueOf(UUID.randomUUID()))
                .attributes(List.of("attribute_id"));
    }

}
