package com.vendo.product_service.domain.image.model;

import lombok.With;

public record Image(
        @With
        String key,

        String contentType,
        long size
) {

    public static Image from(String contentType, long size) {
        return new Image(null, contentType, size);
    }

}
