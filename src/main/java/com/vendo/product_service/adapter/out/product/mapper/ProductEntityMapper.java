package com.vendo.product_service.adapter.out.product.mapper;

import com.vendo.product_service.adapter.common.config.MapStructConfig;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.adapter.model.product.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductEntityMapper {

    ProductEntity toProductEntityFromProductDomain(Product product);
    Product toProductDomainFromProductEntity(ProductEntity productEntity);

}
