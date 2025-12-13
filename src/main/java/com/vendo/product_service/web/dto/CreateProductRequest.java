package com.vendo.product_service.web.dto;

import java.math.BigDecimal;

public record CreateProductRequest(
        String title,
        String description,
        int quantity,
        BigDecimal price,
        String categoryId
) {
}
