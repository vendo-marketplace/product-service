package com.vendo.product_service.adapter.favorite.in.dto;

import com.vendo.product_service.domain.product.model.nested.Address;

import java.math.BigDecimal;

public record FavoriteResponse(
        String id,

        String title,
        BigDecimal price,
        Integer quantity,
        Address address,

        Boolean isNew,
        Boolean active
) {}