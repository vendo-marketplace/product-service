package com.vendo.product_service.common.builder;

import com.vendo.domain.user.common.type.UserRole;
import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.vendo.security.common.type.TokenClaim.*;

@Component
@RequiredArgsConstructor
public class JwtPayloadDataBuilder {

    @Value("${security.jwt.expiration-time}")
    private int JWT_EXPIRATION_TIME;

    private final JwtService jwtService;

    private static final String JWT_USER_SUBJECT = "JWT_USER_SUBJECT";

    private final Map<String, Object> DEFAULT_USER_CLAIMS = buildUserClaims(
            String.valueOf(UUID.randomUUID()),
            true,
            UserStatus.ACTIVE,
            UserRole.USER
        );

    public Map<String, Object> buildUserClaims(String userId, boolean emailVerified, UserStatus userStatus, UserRole userRole) {
        return Map.of(
                USER_ID_CLAIM.getClaim(), userId,
                EMAIL_VERIFIED_CLAIM.getClaim(), emailVerified,
                STATUS_CLAIM.getClaim(), userStatus,
                ROLES_CLAIM.getClaim(), List.of(userRole)
        );
    }

    public Map<String, Object> buildClaimsWithRole(UserRole userRole) {
        Map<String, Object> defaultClaimsCopy = new HashMap<>(Map.copyOf(DEFAULT_USER_CLAIMS));
        defaultClaimsCopy.put(ROLES_CLAIM.getClaim(), List.of(userRole));
        return defaultClaimsCopy;
    }

    public JwtPayload.JwtPayloadBuilder buildValidUserJwtPayload() {
        return JwtPayload.builder()
                .subject(JWT_USER_SUBJECT)
                .claims(DEFAULT_USER_CLAIMS)
                .expiration(JWT_EXPIRATION_TIME)
                .key(jwtService.getSecretKey());
    }
}
