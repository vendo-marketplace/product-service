package com.vendo.product_service.adapter.product.in.dto;

import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.product.model.nested.Address;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record ProductResponse (
        String id,

        String title,
        String description,
        BigDecimal price,
        int quantity,
        boolean isNew,
        boolean active,
        Address address,

        String ownerId,
        String categoryId,

        List<AttributeValue> attributes,
        List<String> imageKeys,

        Instant createdAt
) {
}
