package com.vendo.product_service.adapter.product.out.mapper;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DtoProductMapper {

    Product toEntity(CreateProductRequest request);

    Product toEntity(UpdateProductRequest request);

    ProductResponse toResponse(Product product);

}
