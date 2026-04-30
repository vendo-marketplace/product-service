package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.domain.category.type.CategoryType;

import java.util.List;
import java.util.UUID;

public record CategoryResponseDataBuilder(
) {

    public static CategoryResponse withAllFields() {
        return new CategoryResponse(String.valueOf(UUID.randomUUID()), "Title", "parent_id", CategoryType.SUB, List.of("id_1"), List.of("root_id", "parent_id"));
    }

}
