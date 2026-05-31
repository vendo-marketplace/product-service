package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.user_lib.exception.UserBlockedException;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import com.vendo.user_lib.type.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    public void validateAccess() {
        TokenClaims authClaims = SecurityContextHelper.getAuthClaims();

        if (authClaims.status() == UserStatus.BLOCKED) {
            throw new UserBlockedException("User is blocked.");
        }

        if (!authClaims.emailVerified()) {
            throw new UserEmailNotVerifiedException("User email is not verified.");
        }
    }

}
