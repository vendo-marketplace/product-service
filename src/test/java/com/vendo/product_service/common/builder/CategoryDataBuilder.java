package com.vendo.product_service.common.builder;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CategoryDataBuilder {

    public static Category.CategoryBuilder buildCategoryWithAllFields() {
        AttributeValue attributeValue = AttributeValue.builder()
                .type("string")
                .required(true)
                .allowedValues(List.of("value1", "value2"))
                .build();

        return Category.builder()
                .title("Category title")
                .parentId(String.valueOf(UUID.randomUUID()))
                .categoryType(CategoryType.ROOT)
                .attributes(Map.of("attribute_name", attributeValue));
    }

}
