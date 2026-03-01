package com.vendo.product_service.adapter.out.category.mapper;

import com.vendo.product_service.adapter.model.category.MongoCategory;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import com.vendo.product_service.domain.category.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    Category toEntity(MongoCategory entity);

    MongoCategory toMongoEntity(Category domain);

}
