package com.vendo.product_service.common.mapper;

import com.vendo.product_service.common.config.MapStructConfig;
import com.vendo.product_service.db.model.Product;
import com.vendo.product_service.web.dto.CreateProductRequest;
import com.vendo.product_service.web.dto.ProductResponse;
import com.vendo.product_service.web.dto.UpdateProductRequest;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ProductMapper {

    Product toProductFromCreateProductRequest(CreateProductRequest createProductRequest);

    Product toProductFromUpdateProductRequest(UpdateProductRequest updateProductRequest);

    ProductResponse toProductResponse(Product product);

}
