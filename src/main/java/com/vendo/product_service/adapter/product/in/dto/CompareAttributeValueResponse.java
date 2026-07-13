package com.vendo.product_service.adapter.product.in.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CompareAttributeValueResponse(
        String id,
        String title,
        boolean same,
        List<String> values
) {
}
