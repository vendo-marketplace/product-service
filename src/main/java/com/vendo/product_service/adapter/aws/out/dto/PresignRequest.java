package com.vendo.product_service.adapter.aws.out.dto;

import com.vendo.product_service.adapter.aws.out.dto.nested.PresignBody;
import com.vendo.product_service.adapter.aws.out.dto.nested.PresignType;

import java.util.List;

public record PresignRequest(
        PresignType type,
        List<PresignBody> files
) {
}
