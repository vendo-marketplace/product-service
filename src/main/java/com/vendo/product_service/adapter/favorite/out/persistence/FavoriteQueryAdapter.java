package com.vendo.product_service.adapter.favorite.out.persistence;

import com.vendo.product_service.adapter.favorite.out.mapper.FavoriteMapper;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.port.out.favorite.FavoriteQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FavoriteQueryAdapter implements FavoriteQueryPort {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;


    @Override
    public Optional<Favorite> findByUserIdAndProductId(String userId, String productId) {
        return favoriteRepository.findByUserIdAndProductId(userId, productId)
                .map(favoriteMapper::toFavorite);
    }

    @Override
    public List<Favorite> findAllByUserId(String userId) {
        return favoriteRepository.findAllByUserId(userId)
                .stream()
                .map(favoriteMapper::toFavorite)
                .toList();
    }
}
