package com.vendo.product_service.adapter.image.out.mapper;

import com.vendo.product_service.adapter.image.out.persistence.ProductImageMongo;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ProductImageMapper {

    ProductImageMongo toEntity(Image image);

    List<Image> toProductImage(List<ProductImageMongo> entities);



}
