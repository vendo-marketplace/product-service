package com.vendo.product_service.adapter.product_image.out.mapper;

import com.vendo.product_service.adapter.product_image.out.persistence.ProductImageMongo;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ProductImageMapper {

    ProductImageMongo toEntity(ProductImage productImage);

    List<ProductImage> toProductImage(List<ProductImageMongo> entities);



}
