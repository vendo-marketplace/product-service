package com.vendo.product_service.adapter.favorite.in;

import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.application.favorite.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{productId}")
    public void addFavorite(@PathVariable String productId) {
        favoriteService.add(productId);
    }

    @DeleteMapping("/{productId}")
    public void removeFavorite(@PathVariable String productId) {
        favoriteService.remove(productId);
    }

    @GetMapping
    public List<FavoriteResponse> getFavorites() {
        return favoriteService.getFavorites();
    }

}
