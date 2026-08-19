package com.vendo.product_service.adapter.favorite.out.mapper;

import com.vendo.product_service.adapter.favorite.out.persistence.MongoFavorite;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface FavoriteMapper {

    Favorite toFavorite(MongoFavorite favorite);
    MongoFavorite toEntity(Favorite favorite);

    List<Favorite> toFavorites(List<MongoFavorite> favorites);

}

