package com.vendo.product_service.domain.product.common.mapper;

import com.vendo.product_service.common.config.MapStructConfig;
import com.vendo.product_service.domain.product.db.model.Product;
import com.vendo.product_service.domain.product.web.dto.CreateProductRequest;
import com.vendo.product_service.domain.product.web.dto.ProductResponse;
import com.vendo.product_service.domain.product.web.dto.UpdateProductRequest;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductMapper {

    Product toProductFromCreateProductRequest(CreateProductRequest createProductRequest);

    Product toProductFromUpdateProductRequest(UpdateProductRequest updateProductRequest);

    ProductResponse toProductResponse(Product product);

}
