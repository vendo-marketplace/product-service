package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.out.user.AuthUserPort;
import org.springframework.stereotype.Component;

@Component
public class AuthUserAdapter implements AuthUserPort {

    @Override
    public User getAuthUser() {
        return SecurityContextHelper.getAuthUserFromContext();
    }

}
