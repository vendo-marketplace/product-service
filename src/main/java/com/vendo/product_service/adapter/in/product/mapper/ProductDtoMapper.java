package com.vendo.product_service.adapter.in.product.mapper;

import com.vendo.product_service.adapter.common.config.MapStructConfig;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.adapter.in.product.dto.CreateProductRequest;
import com.vendo.product_service.adapter.in.product.dto.ProductResponse;
import com.vendo.product_service.adapter.in.product.dto.UpdateProductRequest;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductDtoMapper {
    Product toProductDomainFromCreateRequest(CreateProductRequest createProductRequest);

    Product toProductDomainFromUpdateRequest(UpdateProductRequest updateProductRequest);

    ProductResponse toProductResponseFromDomain(Product product);



}
