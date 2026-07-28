package com.vendo.product_service.adapter.favorite.in;

import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.adapter.favorite.in.dto.FavoritesResponse;
import com.vendo.product_service.adapter.favorite.out.mapper.FavoriteMapper;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.favorite.usecase.FavoriteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteUseCase favoriteUseCase;
    private final FavoriteMapper favoriteMapper;

    @PostMapping("/{productId}")
    public void addFavorite(@PathVariable String productId) {
        favoriteUseCase.add(productId);
    }

    @DeleteMapping("/{productId}")
    public void removeFavorite(@PathVariable String productId) {
        favoriteUseCase.remove(productId);
    }

    @GetMapping
    public ResponseEntity<FavoritesResponse> getFavorites() {
        List<Product> products = favoriteUseCase.getAll();
        List<FavoriteResponse> favorites = products.stream().map(favoriteMapper::toResponse).toList();
        return ResponseEntity.ok(FavoritesResponse.of(favorites));
    }

}
