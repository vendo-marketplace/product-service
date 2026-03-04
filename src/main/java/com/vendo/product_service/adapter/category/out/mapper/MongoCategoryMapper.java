package com.vendo.product_service.adapter.category.out.mapper;

import com.vendo.product_service.adapter.category.out.persistence.MongoCategory;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface MongoCategoryMapper {

    Category toCategory(MongoCategory entity);
    MongoCategory toMongoEntity(Category category);

}
