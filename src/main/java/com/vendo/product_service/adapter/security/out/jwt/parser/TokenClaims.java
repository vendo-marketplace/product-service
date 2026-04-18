package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.user_lib.type.UserStatus;

import java.util.List;

public record TokenClaims(
        String userId,
        UserStatus status,
        List<String> roles,
        boolean emailVerified
) {

}
