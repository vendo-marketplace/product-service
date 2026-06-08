package com.vendo.product_service.adapter.favorite.in.dto;


import java.math.BigDecimal;

public record FavoriteResponse(
        String id,
        String title,
        BigDecimal price,
        Integer quantity,
        Boolean active
) {}