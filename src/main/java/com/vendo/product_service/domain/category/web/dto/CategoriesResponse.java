package com.vendo.product_service.domain.category.web.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CategoriesResponse(List<CategoryResponse> items) {
}
