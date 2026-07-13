package com.vendo.product_service.adapter.product.in.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CompareProductResponse(
        String id,
        String title,
        List<CompareAttributeValueResponse> attributes
) {
}
