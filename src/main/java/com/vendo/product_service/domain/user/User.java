package com.vendo.product_service.domain.user;

import com.vendo.user_lib.type.UserStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record User(
        String id,
        String email,
        UserStatus status,
        List<String> roles,
        boolean emailVerified
) {
}