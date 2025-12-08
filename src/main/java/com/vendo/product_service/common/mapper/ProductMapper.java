package com.vendo.product_service.common.mapper;

import com.vendo.product_service.common.config.MapStructConfig;
import com.vendo.product_service.model.Product;
import com.vendo.product_service.web.dto.CreateProductRequest;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductMapper {

    Product toProduct(CreateProductRequest createProductRequest);

}
