package com.vendo.product_service.domain.user;

import com.vendo.user_lib.exception.UserBlockedException;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;

import java.util.List;

public record User(
        String id,
        UserStatus status,
        List<UserRole> roles,
        boolean emailVerified
) {

    public static List<UserRole> toRoles(List<String> roles) {
        return roles.stream()
                .map(UserRole::valueOf)
                .toList();
    }

    public static List<String> toNames(List<UserRole> roles) {
        return roles.stream().map(Enum::name).toList();
    }

    public void throwIfBlocked() {
        if (status == UserStatus.BLOCKED) {
            throw new UserBlockedException("User is blocked.");
        }
    }

    public void throwIfEmailNotVerified() {
        if (!emailVerified) {
            throw new UserEmailNotVerifiedException("User email is not verified.");
        }
    }

}
