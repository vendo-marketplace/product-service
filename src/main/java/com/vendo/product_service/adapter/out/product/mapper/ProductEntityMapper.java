package com.vendo.product_service.adapter.out.product.mapper;

import com.vendo.product_service.infrastructure.config.MapStructConfig;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.adapter.model.product.MongoProduct;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductEntityMapper {

    MongoProduct toProductEntityFromProductDomain(Product product);
    Product toProductDomainFromProductEntity(MongoProduct mongoProduct);

}
