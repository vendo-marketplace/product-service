package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.port.out.user.CurrentUserPort;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserAdapter implements CurrentUserPort {

    @Override
    public String getCurrentUserId() {
        return SecurityContextHelper.getAuthUser().id();
    }

}
