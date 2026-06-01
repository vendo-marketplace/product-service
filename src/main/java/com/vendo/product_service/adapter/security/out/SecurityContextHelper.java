package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.domain.user.User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextHelper {

    public static User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User authUser)) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized.");
        }

        return authUser;
    }
}