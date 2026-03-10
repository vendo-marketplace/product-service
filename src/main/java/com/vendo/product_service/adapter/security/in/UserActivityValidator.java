package com.vendo.product_service.adapter.security.in;

import com.vendo.user_lib.exception.UserBlockedException;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import com.vendo.user_lib.exception.UserIsUnactiveException;
import com.vendo.user_lib.type.UserStatus;

public class UserActivityValidator {

    public static void validate(UserStatus status, boolean emailVerification) {
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
