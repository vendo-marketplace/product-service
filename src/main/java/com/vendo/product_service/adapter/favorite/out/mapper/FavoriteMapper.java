package com.vendo.product_service.adapter.favorite.out.mapper;

import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.adapter.favorite.out.persistence.MongoFavorite;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(config = MapStructConfig.class)
public interface FavoriteMapper {

    Favorite toFavorite(MongoFavorite favorite);

    MongoFavorite toEntity(Favorite favorite);

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "title", source = "product.title")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "quantity", source = "product.quantity")
    @Mapping(target = "active", source = "product.active")
    @Mapping(target = "addedAt", source = "favorite.createdAt")
    FavoriteResponse toResponse(Product product, Favorite favorite);
}

