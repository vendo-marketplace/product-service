package com.vendo.product_service.adapter.security.in.filter.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthInternalException extends AuthenticationException {

    public AuthInternalException(String msg) {
        super(msg);
    }
}
