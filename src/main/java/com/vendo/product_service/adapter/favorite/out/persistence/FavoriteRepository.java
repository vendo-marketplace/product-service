package com.vendo.product_service.adapter.favorite.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FavoriteRepository extends CrudRepository<MongoFavorite, String> {

    List<MongoFavorite> findAllByUserId(String userId);

    void deleteByUserIdAndProductId(
            String userId,
            String productId
    );

    boolean existsByUserIdAndProductId(String userId, String productId);
}
