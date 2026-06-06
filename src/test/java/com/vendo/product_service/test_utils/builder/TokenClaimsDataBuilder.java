package com.vendo.product_service.test_utils.builder;

import com.vendo.core_lib.type.ServiceName;
import com.vendo.core_lib.type.ServiceRole;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;

import java.util.Set;

public class TokenClaimsDataBuilder {

    public static TokenClaims.TokenClaimsBuilder buildWithAllFields() {
        return TokenClaims.builder()
                .subject(ServiceName.INDEXER_SERVICE.toString())
                .audience(Set.of(ServiceName.PRODUCT_SERVICE.toString()))
                .roles(Set.of(ServiceRole.INTERNAL.toString()));
    }

}
