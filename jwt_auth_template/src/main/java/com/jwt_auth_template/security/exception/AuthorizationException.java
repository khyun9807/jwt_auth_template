package com.jwt_auth_template.security.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class AuthorizationException extends AuthenticationException {

    public AuthorizationException(@Nullable String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthorizationException(@Nullable String msg) {
        super(msg);
    }
}
