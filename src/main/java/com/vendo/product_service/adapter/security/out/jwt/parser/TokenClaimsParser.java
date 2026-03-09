package com.vendo.product_service.adapter.security.out.jwt.parser;

public interface TokenClaimsParser {

    TokenClaims extract(String token);
}
