package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.user.User;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;

import java.util.List;

public record UserDataBuilder() {

    public static User.UserBuilder withAllFields() {
        return User.builder()
                .id("id")
                .email("email")
                .status(UserStatus.ACTIVE)
                .roles(List.of(UserRole.USER.name()))
                .emailVerified(true);
    }

}
