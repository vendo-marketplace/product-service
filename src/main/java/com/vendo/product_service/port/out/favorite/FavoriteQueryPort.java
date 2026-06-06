package com.vendo.product_service.port.out.favorite;

import com.vendo.product_service.domain.favorite.model.Favorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteQueryPort {

    Optional<Favorite> findBy(String userId, String productId);

    List<Favorite> findAllBy(String userId);
}
