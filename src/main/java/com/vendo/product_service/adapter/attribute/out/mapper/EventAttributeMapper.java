package com.vendo.product_service.adapter.attribute.out.mapper;

import com.vendo.event_lib.product.nested.AttributeEvent;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface EventAttributeMapper {

    default List<AttributeEvent> toEvents(List<AttributeValue> requestAttributes, List<Attribute> originAttributes) {
        List<AttributeEvent> events = new ArrayList<>();

        for (AttributeValue requestAttribute : requestAttributes) {
            Attribute attribute = requestAttribute.getById(originAttributes);
            events.add(new AttributeEvent(attribute.id(), requestAttribute.values()));
        }

        return events;
    }
}
