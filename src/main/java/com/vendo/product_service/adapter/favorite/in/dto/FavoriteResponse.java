package com.vendo.product_service.adapter.favorite.in.dto;


import java.math.BigDecimal;
import java.time.Instant;

public record FavoriteResponse(
        String id,
        String title,
        BigDecimal price,
        Integer quantity,
        Boolean active,
        Instant addedAt
) {}