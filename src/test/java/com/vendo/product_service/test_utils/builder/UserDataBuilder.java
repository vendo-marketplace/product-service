package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.user.User;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;

import java.util.Set;

public record UserDataBuilder() {

    public static User.UserBuilder withAllFields() {
        return User.builder()
                .id("id")
                .email("email")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(UserRole.USER))
                .emailVerified(true);
    }

}
