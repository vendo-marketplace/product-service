package com.vendo.product_service.domain.user;

import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record User(
        String id,
        String email,
        UserStatus status,
        List<UserRole> roles,
        boolean emailVerified
) {

    public List<String> toRoleNames() {
        return roles.stream()
                .map(Enum::name)
                .toList();
    }

}