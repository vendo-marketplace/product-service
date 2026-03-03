package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.domain.port.security.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.vendo.product_service.adapter.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Component
@RequiredArgsConstructor
public class CurrentUserAdapter implements CurrentUserPort {
    @Override
    public String getCurrentUserId() {
        return getUserIdFromContext();
    }
}
