package com.vendo.product_service.adapter.aws.out.dto;

import java.util.List;

public record ImageValidationRequest(

        List<ImageRequest> images

) {

    public record ImageRequest(
            String key,
            String contentType,
            long size
    ) {}

}
