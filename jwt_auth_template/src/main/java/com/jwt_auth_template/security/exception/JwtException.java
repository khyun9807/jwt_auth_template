package com.jwt_auth_template.security.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class JwtException extends AuthenticationException {
    public JwtException(@Nullable String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtException(@Nullable String msg) {
        super(msg);
    }
}
