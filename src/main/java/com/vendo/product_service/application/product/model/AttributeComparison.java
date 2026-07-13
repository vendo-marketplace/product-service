package com.vendo.product_service.application.product.model;

import java.util.List;

public record AttributeComparison(
        String id,
        String title,
        boolean same,
        List<String> values
) {
}
