package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.model.AttributeDefinition;
import com.vendo.product_service.domain.category.model.AttributeType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateCategoryRequestDataBuilder {

    public static CreateCategoryRequest.CreateCategoryRequestBuilder buildCreateCategoryRequestWithAllFields() {
        AttributeDefinition attributeDefinition = AttributeDefinition.builder()
                .type(AttributeType.STRING)
                .required(false)
                .allowedValues(List.of("allowed_value_1", "allowed_value_2"))
                .build();

        return CreateCategoryRequest.builder()
                .title("CategoryEntity title")
                .parentId("parent_id")
                .code(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("Attribute name", attributeDefinition));
    }

}
