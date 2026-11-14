package com.vendo.product_service.adapter.attribute.out.mapper;

import com.vendo.product_service.adapter.attribute.in.dto.AttributeResponse;
import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.adapter.attribute.in.dto.UpdateAttributeRequest;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DtoAttributeMapper {

    Attribute toAttribute(CreateAttributeRequest request);

    Attribute toAttribute(UpdateAttributeRequest request);

    AttributeResponse toResponse(Attribute attribute);

    List<AttributeResponse> toResponses(List<Attribute> attributes);

}
