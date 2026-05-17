package com.vendo.product_service.adapter.security.out.jwt.parser;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record InternalTokenClaims(
        String subject,
        List<String> roles,
        Set<String> audience) {
}

