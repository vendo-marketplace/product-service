package com.vendo.product_service.adapter.in.product.mapper;

import com.vendo.product_service.adapter.in.product.dto.CreateProductRequest;
import com.vendo.product_service.adapter.in.product.dto.ProductResponse;
import com.vendo.product_service.adapter.in.product.dto.UpdateProductRequest;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductMapper {

    Product toEntity(CreateProductRequest request);
    Product toEntity(UpdateProductRequest request);
    ProductResponse toResponse(Product product);

}
