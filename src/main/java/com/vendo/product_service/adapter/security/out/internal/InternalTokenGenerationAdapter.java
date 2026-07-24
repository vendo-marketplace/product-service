package com.vendo.product_service.adapter.security.out.internal;

import com.vendo.core_lib.type.ServiceName;
import com.vendo.core_lib.type.ServiceRole;
import com.vendo.product_service.adapter.security.out.props.JwtProperties;
import com.vendo.security_lib.type.TokenClaim;
import com.vendo.security_starter.jwt.JwtPayload;
import com.vendo.security_starter.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InternalTokenGenerationAdapter implements InternalTokenGenerationPort {

    private final JwtProperties props;

    @Override
    public String generate() {
        JwtProperties.Internal internal = props.getInternal();

        JwtPayload jwtPayload = JwtPayload.builder()
                .subject(ServiceName.PRODUCT_SERVICE.getServiceName())
                .audience(Set.of(ServiceName.AWS_SERVICE.getServiceName()))
                .claims(Map.of(TokenClaim.ROLES.getClaim(), ServiceRole.INTERNAL.name()))
                .expiration(internal.expirationTime())
                .build();
        return JwtService.buildToken(jwtPayload, internal.key());
    }

}
