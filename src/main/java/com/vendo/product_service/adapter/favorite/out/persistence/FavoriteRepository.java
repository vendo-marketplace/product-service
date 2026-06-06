package com.vendo.product_service.adapter.favorite.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends MongoRepository<MongoFavorite, String> {

    Optional<MongoFavorite> findByUserIdAndProductId(
            String userId,
            String productId
    );

    List<MongoFavorite> findAllByUserId(String userId);

    void deleteByUserIdAndProductId(
            String userId,
            String productId
    );
}
