package com.vendo.product_service.adapter.product.out.mapper;

import com.vendo.product_service.adapter.product.out.persistence.MongoProduct;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface MongoProductMapper {

    MongoProduct toMongoProduct(Product product);

    Product toProduct(MongoProduct mongoProduct);

}
