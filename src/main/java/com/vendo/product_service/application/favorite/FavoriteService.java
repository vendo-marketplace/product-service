package com.vendo.product_service.application.favorite;

import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.adapter.favorite.out.mapper.FavoriteMapper;
import com.vendo.product_service.domain.favorite.exception.FavoriteAlreadyExistsException;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.favorite.FavoriteUseCase;
import com.vendo.product_service.port.out.favorite.FavoriteCommandPort;
import com.vendo.product_service.port.out.favorite.FavoriteQueryPort;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.port.out.user.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService implements FavoriteUseCase {

    private final FavoriteCommandPort favoriteCommandPort;
    private final FavoriteQueryPort favoriteQueryPort;
    private final FavoriteMapper favoriteMapper;
    private final ProductQueryPort productQueryPort;
    private final CurrentUserPort currentUserPort;

    @Override
    public void add(String productId) {

        String userId = currentUserPort.getCurrentUserId();

        if(!productQueryPort.existsById(productId)) {
            throw new ProductNotFoundException("Product with id: " + productId + " not found");
        }

        favoriteQueryPort.findBy(userId, productId)
                .ifPresent(f -> {
            throw new FavoriteAlreadyExistsException(
                    "Product with id: " + productId + " is already in favorites."
            );
        });

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .productId(productId)
                .build();

        favoriteCommandPort.save(favorite);
    }

    @Override
    public void remove(String productId) {
        favoriteCommandPort.delete(
                currentUserPort.getCurrentUserId(),
                productId
        );
    }

    @Override
    public List<FavoriteResponse> getAll() {

        String userId = currentUserPort.getCurrentUserId();

        List<Favorite> favorites = favoriteQueryPort.findAllBy(userId);

        if (favorites.isEmpty()) {
            return List.of();
        }

        List<String> productIds = favorites.stream()
                .map(Favorite::getProductId)
                .toList();

        List<Product> products = productQueryPort.findAllById(productIds);

        Map<String, Favorite> favoriteByProductId = favorites.stream()
                .collect(Collectors.toMap(
                        Favorite::getProductId,
                        f -> f
                ));

        return products.stream()
                .map(product -> favoriteMapper.toResponse(
                        product,
                        favoriteByProductId.get(product.getId())
                ))
                .toList();
    }
}
