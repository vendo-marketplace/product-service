package com.vendo.product_service.adapter.favorite.in;

import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.port.in.favorite.FavoriteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteUseCase favoriteUseCase;

    @PostMapping("/{productId}")
    public void addFavorite(@PathVariable String productId) {
        favoriteUseCase.add(productId);
    }

    @DeleteMapping("/{productId}")
    public void removeFavorite(@PathVariable String productId) {
        favoriteUseCase.remove(productId);
    }

    @GetMapping
    public List<FavoriteResponse> getFavorites() {
        return favoriteUseCase.getAll();
    }

}
