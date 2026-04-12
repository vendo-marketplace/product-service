package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.user_lib.type.UserStatus;

import java.util.List;

public record UserTokenClaims(
        String userId,
        UserStatus status,
        List<String> roles,
        boolean emailVerification
) {

    public boolean isAuthCompleted() {
        return emailVerification && status == UserStatus.ACTIVE;
    }

}
