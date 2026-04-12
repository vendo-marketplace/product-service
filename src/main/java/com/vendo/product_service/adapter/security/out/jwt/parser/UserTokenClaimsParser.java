package com.vendo.product_service.adapter.security.out.jwt.parser;

public interface UserTokenClaimsParser {

    UserTokenClaims extract(String token);
}
