package com.vendo.product_service.adapter.security.out.jwt.parser;

import lombok.Builder;

import java.util.Set;

@Builder
public record TokenClaims(
        String subject,
        Set<String> roles,
        Set<String> audience
) {

}
