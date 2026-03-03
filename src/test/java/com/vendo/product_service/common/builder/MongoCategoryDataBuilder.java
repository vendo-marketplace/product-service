package com.vendo.product_service.common.builder;

import com.vendo.product_service.adapter.category.out.persistence.MongoCategory;
import com.vendo.product_service.domain.category.model.AttributeDefinition;
import com.vendo.product_service.domain.category.model.AttributeType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MongoCategoryDataBuilder {

    public static MongoCategory.MongoCategoryBuilder<?, ?> buildCategoryWithAllFields() {
        AttributeDefinition attributeDefinition = AttributeDefinition.builder()
                .type(AttributeType.STRING)
                .required(true)
                .allowedValues(List.of("value1", "value2"))
                .build();

        return MongoCategory.builder()
                .title("CategoryEntity title")
                .parentId(String.valueOf(UUID.randomUUID()))
                .code(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("Attribute", attributeDefinition));
    }

}
