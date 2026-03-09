package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.user_lib.exception.UserBlockedException;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import com.vendo.user_lib.exception.UserIsUnactiveException;
import com.vendo.user_lib.type.UserStatus;

import java.util.List;

public record TokenClaims(
        String userId,
        UserStatus status,
        List<String> roles,
        boolean emailVerification
) {

    public void validateActivity() {
        if (status == UserStatus.BLOCKED) {
            throw new UserBlockedException("User is blocked.");
        }

        if (!emailVerification) {
            throw new UserEmailNotVerifiedException("User email is not verified.");
        }

        if (status != UserStatus.ACTIVE) {
            throw new UserIsUnactiveException("User is unactive.");
        }
    }

}
