package com.vendo.product_service.adapter.favorite.in.dto;

import java.util.List;

public record FavoritesResponse(List<FavoriteResponse> data) {

    public static FavoritesResponse of(List<FavoriteResponse> data) {
        return new FavoritesResponse(data);
    }

}
