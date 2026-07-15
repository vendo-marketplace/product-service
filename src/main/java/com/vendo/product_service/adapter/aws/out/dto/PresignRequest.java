package com.vendo.product_service.adapter.aws.out.dto;

public record PresignRequest(
        String id,
        String contentType
) {
}
