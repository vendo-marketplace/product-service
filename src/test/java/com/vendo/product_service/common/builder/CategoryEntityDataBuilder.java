package com.vendo.product_service.common.builder;

import com.vendo.product_service.adapter.model.category.CategoryEntity;
import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import com.vendo.product_service.adapter.model.category.embedded.AttributeType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CategoryEntityDataBuilder {

    public static CategoryEntity.CategoryEntityBuilder<?, ?> buildCategoryWithAllFields() {
        AttributeDefinition attributeDefinition = AttributeDefinition.builder()
                .type(AttributeType.STRING)
                .required(true)
                .allowedValues(List.of("value1", "value2"))
                .build();

        return CategoryEntity.builder()
                .title("CategoryEntity title")
                .parentId(String.valueOf(UUID.randomUUID()))
                .code(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("Attribute", attributeDefinition));
    }

}
