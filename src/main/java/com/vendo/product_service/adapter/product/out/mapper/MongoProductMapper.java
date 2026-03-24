package com.vendo.product_service.adapter.product.out.mapper;

import com.vendo.product_service.adapter.product.out.persistence.MongoProduct;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class)
public interface MongoProductMapper {

    MongoProduct toEntity(Product product);

    Product toProduct(MongoProduct entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget MongoProduct entity, Product product);

}
