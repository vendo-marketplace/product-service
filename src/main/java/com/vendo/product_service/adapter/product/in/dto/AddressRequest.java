package com.vendo.product_service.adapter.product.in.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record AddressRequest(
        @NotNull(message = "City is required.")
        @Size(min = 2, max = 100, message = "City should have from 2 to 100 characters.")
        String city,

        @NotNull(message = "Region is required.")
        @Size(min = 2, max = 100, message = "Region should have from 2 to 100 characters.")
        String region,

        @Valid
        @NotNull(message = "Location is required.")
        AddressRequest.LocationRequest location
) {

    public record LocationRequest(

            @NotNull(message = "Latitude is required.")
            @DecimalMin(value = "-90.0", message = "Minimal latitude should be -90.")
            @DecimalMax(value = "90.0", message = "Maximum latitude should be 90.")
            Double lat,

            @NotNull(message = "Longitude is required.")
            @DecimalMin(value = "-180.0", message = "Minimal longitude should be -180.")
            @DecimalMax(value = "180.0", message = "Maximum longitude should be 180.")
            Double lon
    ) {}

}
