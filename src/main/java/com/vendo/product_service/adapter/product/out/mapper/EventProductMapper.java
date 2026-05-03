package com.vendo.product_service.adapter.product.out.mapper;

import com.vendo.event_lib.product.ProductCreatedEvent;
import com.vendo.event_lib.product.ProductUpdatedEvent;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface EventProductMapper {

    @Mapping(target = "attributes", source = "attributes")
    ProductCreatedEvent toCreatedEvent(Product product, List<Attribute> attributes);

    @Mapping(target = "attributes", source = "attributes")
    ProductUpdatedEvent toUpdatedEvent(Product product, List<Attribute> attributes);

}
