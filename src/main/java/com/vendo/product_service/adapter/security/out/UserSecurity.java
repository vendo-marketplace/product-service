package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.adapter.security.out.jwt.parser.UserTokenClaims;
import com.vendo.user_lib.exception.UserBlockedException;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import com.vendo.user_lib.exception.UserIsUnactiveException;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public void validateAuthCompleted(Authentication auth) {
        UserTokenClaims claims = (UserTokenClaims) auth.getPrincipal();

        if (claims.status() == UserStatus.BLOCKED) {
            throw new UserBlockedException("User is blocked.");
        }

        if (!claims.emailVerification()) {
            throw new UserEmailNotVerifiedException("User email is not verified.");
        }

        if (claims.status() != UserStatus.ACTIVE) {
            throw new UserIsUnactiveException("User is unactive.");
        }

    }

    public void validateAuthCompletedAdmin(Authentication auth) {
        UserTokenClaims claims = (UserTokenClaims) auth.getPrincipal();

        if (!claims.roles().contains(UserRole.ADMIN.name()))
            throw new AccessDeniedException("Resource is unreachable.");

        validateAuthCompleted(auth);
    }

}
