package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.product_service.domain.user.User;

public interface AuthenticationParser {

    User extract(String token);

}
