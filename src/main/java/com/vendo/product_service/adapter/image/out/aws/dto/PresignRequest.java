package com.vendo.product_service.adapter.image.out.aws.dto;

import com.vendo.product_service.adapter.image.out.aws.dto.nested.PresignBody;
import com.vendo.product_service.domain.image.model.PresignType;

import java.util.List;

public record PresignRequest(
        PresignType type,
        List<PresignBody> files
) {
}
