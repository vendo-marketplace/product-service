package com.vendo.product_service.common.builder;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CategoryDataBuilder {

    public static Category.CategoryBuilder buildCategoryWithAllFields() {
        AttributeDefinition attributeDefinition = AttributeDefinition.builder()
                .type(AttributeType.STRING)
                .required(true)
                .allowedValues(List.of("value1", "value2"))
                .build();

        return Category.builder()
                .title("Category title")
                .parentId(String.valueOf(UUID.randomUUID()))
                .categoryType(CategoryType.ROOT)
                .attributes(Map.of("attribute_name", attributeDefinition));
    }

}
