package com.vendo.product_service.adapter.image.out.mapper;

import com.vendo.event_lib.product.ProductImageRequestedEvent;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductImageEventMapper {

    Image toProductImage(ProductImageRequestedEvent event);

}
