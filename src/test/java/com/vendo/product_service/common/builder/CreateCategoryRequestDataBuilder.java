package com.vendo.product_service.common.builder;

import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.db.model.embedded.AttributeValue;
import com.vendo.product_service.web.dto.CreateCategoryRequest;

import java.util.List;
import java.util.Map;

public class CreateCategoryRequestDataBuilder {

    public static CreateCategoryRequest.CreateCategoryRequestBuilder buildCreateCategoryRequestWithAllFields() {
        AttributeValue attributeValue = AttributeValue.builder()
                .type("attribute_type")
                .required(false)
                .allowedValues(List.of("allowed_value_1", "allowed_value_2"))
                .build();

        return CreateCategoryRequest.builder()
                .title("Category title")
                .parentId("parent_id")
                .categoryType(CategoryType.ROOT)
                .attributes(Map.of("attribute_name", attributeValue));
    }

}
