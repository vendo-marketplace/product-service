package com.vendo.product_service.adapter.favorite.out.mapper;

import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.adapter.favorite.out.persistence.MongoFavorite;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;


@Mapper(config = MapStructConfig.class)
public interface FavoriteMapper {

    Favorite toFavorite(MongoFavorite favorite);

    MongoFavorite toEntity(Favorite favorite);

    FavoriteResponse toResponse(Product product);
}

