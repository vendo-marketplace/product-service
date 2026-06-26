package com.vendo.product_service.adapter.favorite.out.persistence;

import com.vendo.product_service.adapter.favorite.out.mapper.FavoriteMapper;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.port.out.favorite.FavoriteQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoriteQueryAdapter implements FavoriteQueryPort {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    @Override
    public List<Favorite> findAllBy(String userId) {
        return favoriteRepository.findAllByUserId(userId)
                .stream()
                .map(favoriteMapper::toFavorite)
                .toList();
    }

    @Override
    public boolean existsBy(String userId, String productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId,productId);
    }
}
