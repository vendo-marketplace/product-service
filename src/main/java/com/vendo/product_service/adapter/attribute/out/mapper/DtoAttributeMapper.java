package com.vendo.product_service.adapter.attribute.out.mapper;

import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DtoAttributeMapper {

    Attribute toAttribute(CreateAttributeRequest request);

}
