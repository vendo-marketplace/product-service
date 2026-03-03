package com.vendo.product_service.adapter.category.out.mapper;

import com.vendo.product_service.adapter.category.out.persistence.MongoCategory;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    Category toEntity(MongoCategory entity);

    MongoCategory toMongoEntity(Category domain);

}
