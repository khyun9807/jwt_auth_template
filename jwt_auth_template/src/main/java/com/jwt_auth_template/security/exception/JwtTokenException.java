package com.jwt_auth_template.security.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class JwtTokenException extends AuthenticationException {
    public JwtTokenException(@Nullable String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtTokenException(@Nullable String msg) {
        super(msg);
    }
}
