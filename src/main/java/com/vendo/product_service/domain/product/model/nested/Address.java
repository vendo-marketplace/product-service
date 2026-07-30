package com.vendo.product_service.domain.product.model.nested;

public record Address(
        String region,
        String city,
        Location location
) {

    public record Location(
            double lat,
            double lon
    ) {}

}
