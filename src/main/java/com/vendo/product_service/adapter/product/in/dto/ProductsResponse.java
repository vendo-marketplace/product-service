package com.vendo.product_service.adapter.product.in.dto;

import java.util.List;

public record ProductsResponse(List<ProductResponse> data) {

    public static ProductsResponse of(List<ProductResponse> data) {
        return new ProductsResponse(data);
    }

}
