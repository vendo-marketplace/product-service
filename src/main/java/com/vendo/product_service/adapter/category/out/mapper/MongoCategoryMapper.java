package com.vendo.product_service.adapter.category.out.mapper;

import com.vendo.product_service.adapter.category.out.persistence.MongoCategory;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class)
public interface MongoCategoryMapper {

    Category toCategory(MongoCategory entity);
    MongoCategory toEntity(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget MongoCategory entity, Category category);

}
