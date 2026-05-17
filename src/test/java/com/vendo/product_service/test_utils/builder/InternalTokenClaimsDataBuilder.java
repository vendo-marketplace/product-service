package com.vendo.product_service.test_utils.builder;

import com.vendo.core_lib.type.ServiceName;
import com.vendo.core_lib.type.ServiceRole;
import com.vendo.product_service.adapter.security.out.jwt.parser.InternalTokenClaims;

import java.util.List;
import java.util.Set;

public class InternalTokenClaimsDataBuilder {

    public static InternalTokenClaims.InternalTokenClaimsBuilder buildWithAllFields() {
        return InternalTokenClaims.builder()
                .subject(ServiceName.INDEXER_SERVICE.toString())
                .audience(Set.of(ServiceName.PRODUCT_SERVICE.toString()))
                .roles(List.of(ServiceRole.INTERNAL.toString()));
    }

}
