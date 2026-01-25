package com.jwt_auth_template.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class MemberAuthenticationException extends AuthenticationException {
    private final ErrorCode errorCode;

    public MemberAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
