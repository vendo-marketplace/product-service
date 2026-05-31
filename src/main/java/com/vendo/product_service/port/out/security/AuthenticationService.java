package com.vendo.product_service.port.out.security;

import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;

public interface AuthenticationService {

    TokenClaims getAuthClaims();

}
