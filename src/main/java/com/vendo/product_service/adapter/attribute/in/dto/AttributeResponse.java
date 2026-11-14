package com.vendo.product_service.adapter.attribute.in.dto;

import com.vendo.product_service.domain.attribute.model.AttributeType;
import lombok.Builder;

import java.util.List;

@Builder
public record AttributeResponse(
        String id,
        String title,
        String slug,
        AttributeType type,
        boolean required,
        List<String> allowedValues
) {
}
