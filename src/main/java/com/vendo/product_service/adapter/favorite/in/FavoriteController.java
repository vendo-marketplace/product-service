package com.vendo.product_service.adapter.favorite.in;

import com.vendo.core_lib.response.DataResponse;
import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.adapter.favorite.out.mapper.DtoFavoriteMapper;
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
    private final DtoFavoriteMapper mapper;

    @PostMapping("/{productId}")
    public void addFavorite(@PathVariable String productId) {
        favoriteUseCase.add(productId);
    }

    @DeleteMapping("/{productId}")
    public void removeFavorite(@PathVariable String productId) {
        favoriteUseCase.remove(productId);
    }

    @GetMapping
    public ResponseEntity<DataResponse<FavoriteResponse>> getFavorites() {
        List<Product> products = favoriteUseCase.getAll();
        return ResponseEntity.ok(new DataResponse<>(mapper.toResponses(products)));
    }

}
