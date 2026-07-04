package com.vendo.product_service.application.product.model;

import java.util.List;

public record ProductComparison(
        String id,
        String title,
        boolean same,
        List<List<String>> values
) {
}
