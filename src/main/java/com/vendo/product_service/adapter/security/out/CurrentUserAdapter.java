package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.port.user.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserAdapter implements CurrentUserPort {

    @Override
    public String getCurrentUserId() {
        return SecurityContextHelper.getUserIdFromContext();
    }

}
