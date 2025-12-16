package com.vendo.product_service.common.builder;

import com.vendo.product_service.db.model.Product;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class ProductDataBuilder {

    public static Product.ProductBuilder buildProductWithRequiredFields() {
        return Product.builder()
                .title("Product title")
                .description("Product description")
                .quantity(1)
                .price(BigDecimal.ONE)
                .categoryId(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("attribute_name", "attribute_value"));
    }

}
