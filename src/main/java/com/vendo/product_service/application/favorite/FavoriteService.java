package com.vendo.product_service.application.favorite;

import com.vendo.product_service.domain.favorite.exception.FavoriteNotFoundException;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.favorite.FavoriteUseCase;
import com.vendo.product_service.port.favorite.FavoriteCommandPort;
import com.vendo.product_service.port.favorite.FavoriteQueryPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService implements FavoriteUseCase {

    private final FavoriteCommandPort favoriteCommandPort;
    private final FavoriteQueryPort favoriteQueryPort;

    private final ProductQueryPort productQueryPort;
    private final AuthUserPort authUserPort;

    @Override
    public void add(String productId) {
        if (!productQueryPort.existsById(productId)) {
            throw new ProductNotFoundException("Product not found.");
        }

        Favorite favorite = Favorite.builder()
                .userId(authUserPort.getAuthUser().id())
                .productId(productId)
                .build();
        favoriteCommandPort.save(favorite);
    }

    @Override
    public void remove(String productId) {
        String userId = authUserPort.getAuthUser().id();

        if (!favoriteQueryPort.existsBy(userId, productId)) {
            throw new FavoriteNotFoundException("Favorite not found.");
        }

        favoriteCommandPort.delete(userId, productId);
    }

    @Override
    public List<Product> getAll() {
        List<Favorite> favorites = favoriteQueryPort.findAllBy(authUserPort.getAuthUser().id());
        if (favorites.isEmpty()) return List.of();

        List<String> productIds = favorites.stream().map(Favorite::productId).toList();
        return productQueryPort.findAllByIds(productIds);
    }
}
