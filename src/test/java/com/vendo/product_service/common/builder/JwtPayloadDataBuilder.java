package com.vendo.product_service.common.builder;

import com.vendo.domain.user.common.type.UserRole;
import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.vendo.security.common.type.TokenClaim.*;

@Component
@RequiredArgsConstructor
public class JwtPayloadDataBuilder {

    @Value("${security.jwt.expiration-time}")
    private int JWT_EXPIRATION_TIME;

    private static final String JWT_USER_SUBJECT = "JWT_USER_SUBJECT";

    private final JwtService jwtService;

    public JwtPayload.JwtPayloadBuilder buildValidUserJwtPayload() {
        Map<String, Object> claims = Map.of(
                USER_ID_CLAIM.getClaim(), String.valueOf(UUID.randomUUID()),
                EMAIL_VERIFIED_CLAIM.getClaim(), true,
                STATUS_CLAIM.getClaim(), UserStatus.ACTIVE,
                ROLES_CLAIM.getClaim(), List.of(UserRole.USER)
        );
        return JwtPayload.builder()
                .subject(JWT_USER_SUBJECT)
                .claims(claims)
                .expiration(JWT_EXPIRATION_TIME)
                .key(jwtService.getSecretKey());
    }
}
