package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.category.model.AttributeDefinition;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.category.model.Category;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CategoryDataBuilder {

    public static Category.Builder withAllFields() {
        AttributeDefinition attributeDefinition = AttributeDefinition.builder()
                .type(AttributeType.STRING)
                .required(false)
                .allowedValues(List.of("allowed_value_1", "allowed_value_2"))
                .build();

        return Category.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .title("Category")
                .parentId("parent_id")
                .code(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("Attribute", attributeDefinition));
    }

}
