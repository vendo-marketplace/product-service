package com.vendo.product_service.adapter.product_image.out.mapper;

import com.vendo.event_lib.product.ProductImageRequestedEvent;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductImageEventMapper {

    ProductImage toProductImage(ProductImageRequestedEvent event);

}
