package com.vendo.product_service.adapter.favorite.out.persistence;

import com.vendo.product_service.adapter.favorite.out.mapper.FavoriteMapper;
import com.vendo.product_service.domain.favorite.exception.FavoriteAlreadyExistsException;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.port.out.favorite.FavoriteCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteCommandAdapter implements FavoriteCommandPort {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;


    @Override
    public void save(Favorite favorite) {
        try {
           favoriteRepository.save(favoriteMapper.toEntity(favorite));
        } catch (DuplicateKeyException e) {
            throw new FavoriteAlreadyExistsException("Product is already in favorites");
        }
    }

    @Override
    public void delete(String userId, String productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
