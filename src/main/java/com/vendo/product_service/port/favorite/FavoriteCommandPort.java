package com.vendo.product_service.port.favorite;

import com.vendo.product_service.domain.favorite.model.Favorite;

public interface FavoriteCommandPort {

    void save(Favorite favorite);

    void delete(String userId, String productId);
}
