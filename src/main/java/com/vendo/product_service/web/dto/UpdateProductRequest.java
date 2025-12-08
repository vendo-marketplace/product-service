package com.vendo.product_service.web.dto;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateProductRequest(
        String title,
        String description,
        Integer quantity,
        BigDecimal price,
        String categoryId,
        Map<String, Object> attributes,
        Boolean active) {
}
