package com.vendo.product_service.port.out.favorite;

import com.vendo.product_service.domain.favorite.model.Favorite;

import java.util.List;

public interface FavoriteQueryPort {
    List<Favorite> findAllBy(String userId);

    boolean existsByUserIdAndProductId(String userId, String productId);
}
