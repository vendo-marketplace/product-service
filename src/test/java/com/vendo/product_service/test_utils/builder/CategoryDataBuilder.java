package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.category.model.Category;

import java.util.List;
import java.util.UUID;

public class CategoryDataBuilder {

    public static Category.CategoryBuilder withAllFields() {
        return Category.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .title("Category")
                .parentId("parent_id")
                .slug("slug")
                .attributes(List.of("id_1"));
    }

}
