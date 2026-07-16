package com.vendo.product_service.adapter.aws.out.dto;

import com.vendo.product_service.domain.image.model.PresignedImage;

import java.util.List;

public record PresignResponse(
        List<PresignedImage> data
) {
}
