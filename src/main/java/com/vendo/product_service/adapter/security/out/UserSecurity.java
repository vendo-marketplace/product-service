package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    public void validateAccess() {
        User authUser = SecurityContextHelper.getAuthUser();
        authUser.throwIfBlocked();
        authUser.throwIfEmailNotVerified();
    }
}
