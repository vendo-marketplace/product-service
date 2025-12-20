package com.vendo.product_service.common.builder;

import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.db.model.embedded.AttributeType;
import com.vendo.product_service.web.dto.CreateCategoryRequest;

import java.util.List;
import java.util.Map;

public class CreateCategoryRequestDataBuilder {

    public static CreateCategoryRequest.CreateCategoryRequestBuilder buildCreateCategoryRequestWithAllFields() {
        AttributeDefinition attributeDefinition = AttributeDefinition.builder()
                .type(AttributeType.STRING)
                .required(false)
                .allowedValues(List.of("allowed_value_1", "allowed_value_2"))
                .build();

        return CreateCategoryRequest.builder()
                .title("Category title")
                .parentId("parent_id")
                .categoryType(CategoryType.ROOT)
                .attributes(Map.of("attribute_name", attributeDefinition));
    }

}
