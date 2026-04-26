package com.vendo.product_service.adapter.category.in.dto;

import com.vendo.product_service.domain.category.type.CategoryType;

import java.util.List;

public record CategoryResponse(
         String id,
         String title,
         String parentId,
         CategoryType categoryType,
         List<String> attributes) {
}
