package com.vendo.product_service.common.builder;

import com.vendo.product_service.adapter.model.product.ProductEntity;
import com.vendo.product_service.domain.product.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductDataBuilder {

    public static ProductEntity.ProductEntityBuilder buildProductWithRequiredFields() {
        return ProductEntity.builder()
                .title("Product title")
                .description("Product description")
                .quantity(1)
                .price(BigDecimal.ONE)
                .categoryId(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("attribute_name", List.of("attribute_value")))
                .active(true);
    }

}
