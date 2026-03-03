package com.vendo.product_service.adapter.product.in.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ProductResponse (
        String id,
        String title,
        String description,
        int quantity,
        BigDecimal price,
        String ownerId,
        String categoryId,
        Map<String, Object> attributes,
        boolean active
) {
}
