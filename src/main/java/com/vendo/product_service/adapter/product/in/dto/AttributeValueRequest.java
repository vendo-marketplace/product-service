package com.vendo.product_service.adapter.product.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record AttributeValueRequest(

        @NotBlank(message = "Attribute id is required.")
        String id,

        @NotNull(message = "Attribute values are required.")
        List<@NotNull(message = "Attribute value must not be null.") String> values
) {
}
