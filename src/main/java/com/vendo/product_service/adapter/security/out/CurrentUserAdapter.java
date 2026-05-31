package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.port.out.security.AuthenticationService;
import com.vendo.product_service.port.out.user.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserAdapter implements CurrentUserPort {

    private final AuthenticationService authenticationService;

    @Override
    public String getCurrentUserId() {
        return authenticationService.getAuthClaims().userId();
    }

}
