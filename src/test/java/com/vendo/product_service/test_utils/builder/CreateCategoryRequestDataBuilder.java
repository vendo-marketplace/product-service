package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;

import java.util.List;

public class CreateCategoryRequestDataBuilder {

    public static CreateCategoryRequest.CreateCategoryRequestBuilder withAllFields() {
        return CreateCategoryRequest.builder()
                .title("Title")
                .parentId("parent_id")
                .slug("slug")
                .attributes(List.of("id_1"));
    }

}
