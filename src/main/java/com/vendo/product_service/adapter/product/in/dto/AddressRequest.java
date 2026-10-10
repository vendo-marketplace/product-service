package com.vendo.product_service.adapter.product.in.dto;

import jakarta.validation.constraints.*;

public record AddressRequest(
        @NotNull(message = "City is required.")
        @Size(min = 2, max = 100, message = "City should have from 2 to 100 characters.")
        String city
) {
}
