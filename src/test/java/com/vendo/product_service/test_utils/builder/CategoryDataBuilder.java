package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.category.model.Category;

import java.util.List;
import java.util.UUID;

public class CategoryDataBuilder {

    public static Category.Builder withAllFields() {
        return Category.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .title("Category")
                .parentId("parent_id")
                .code(String.valueOf(UUID.randomUUID()))
                .attributes(List.of("id_1", "id_2", "id_3"));
    }

}
