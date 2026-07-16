package com.vendo.product_service.adapter.aws.out.dto;

import com.vendo.product_service.domain.image.model.PresignImage;

import java.util.List;

public record PresignResponse(
        List<PresignImage> data
) {
}
