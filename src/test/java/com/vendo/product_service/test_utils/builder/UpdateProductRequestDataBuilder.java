package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;

import java.math.BigDecimal;
import java.util.List;

public class UpdateProductRequestDataBuilder {

    public static UpdateProductRequest.UpdateProductRequestBuilder withAllFields() {
        return UpdateProductRequest.builder()
                .title("Title")
                .description("Description")
                .quantity(1)
                .isNew(true)
                .price(BigDecimal.TEN)
                .categoryId(null)
                .attributes(List.of(AttributeValueRequestDataBuilder.withAllFields().build()))
                .active(false);
    }

}
