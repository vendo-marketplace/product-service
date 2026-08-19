package com.vendo.product_service.adapter.favorite.in.dto;

import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.product.model.nested.Address;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record FavoriteResponse(
        String id,

        String title,
        String description,
        Integer quantity,
        BigDecimal price,
        Address address,

        String ownerId,
        String categoryId,

        Boolean isNew,
        Boolean active,

        List<AttributeValue> attributes,
        List<String> images,

        Instant createdAt
) {
}