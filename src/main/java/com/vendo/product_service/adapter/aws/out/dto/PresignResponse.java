package com.vendo.product_service.adapter.aws.out.dto;

import java.util.List;

public record PresignResponse(
        List<PresignBody> data
) {

    record PresignBody(
            String id,
            String uploadUrl,
            String key) {
    }
}
