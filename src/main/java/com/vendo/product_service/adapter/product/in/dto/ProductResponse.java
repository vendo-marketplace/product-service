package com.vendo.product_service.adapter.product.in.dto;

import com.vendo.product_service.domain.attribute.model.AttributeValue;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductResponse (
        String id,
        String title,
        String description,
        int quantity,
        BigDecimal price,
        String ownerId,
        String categoryId,
        List<AttributeValue> attributes,
        boolean active,
        Instant createdAt
) {
}
