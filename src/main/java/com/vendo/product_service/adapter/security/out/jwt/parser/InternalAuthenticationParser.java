package com.vendo.product_service.adapter.security.out.jwt.parser;

public interface InternalAuthenticationParser {

    TokenClaims extract(String token);

}
