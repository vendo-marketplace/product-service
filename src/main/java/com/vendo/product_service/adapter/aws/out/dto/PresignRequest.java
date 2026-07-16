package com.vendo.product_service.adapter.aws.out.dto;

import java.util.List;

public record PresignRequest(
        PresignType type,
        List<PresignBody> images
) {

    public record PresignBody(
            String id,
            String contentType
    ) {
    }

}
