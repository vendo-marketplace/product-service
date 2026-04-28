package com.vendo.product_service.adapter.attribute.out.mapper;

import com.vendo.product_service.adapter.attribute.out.persistence.MongoAttribute;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface MongoAttributeMapper {

    Attribute toAttribute(MongoAttribute entity);
    MongoAttribute toEntity(Attribute attribute);

}
