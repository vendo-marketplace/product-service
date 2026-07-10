package com.vendo.product_service.domain.favorite.model;

import lombok.Builder;

@Builder
public record Favorite (

    String id,
    String userId,
    String productId

) {


}
