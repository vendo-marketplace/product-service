package com.vendo.product_service.test_utils.builder;

import com.vendo.core_lib.type.ServiceName;
import com.vendo.core_lib.type.ServiceRole;
import com.vendo.security_starter.jwt.parser.TokenClaims;

import java.util.Set;

public class TokenClaimsDataBuilder {

    public static TokenClaims.Builder buildWithAllFields() {
        return TokenClaims.builder()
                .subject(ServiceName.AUTH_SERVICE.getServiceName())
                .audience(Set.of(ServiceName.PRODUCT_SERVICE.toString()))
                .roles(Set.of(ServiceRole.INTERNAL.toString()));
    }

}
