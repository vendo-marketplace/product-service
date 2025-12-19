package com.vendo.product_service.web.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record UpdateProductRequest(
        String title,
        String description,
        Integer quantity,
        BigDecimal price,
        String categoryId,
        Map<String, Object> attributes,
        Boolean active) {
}
