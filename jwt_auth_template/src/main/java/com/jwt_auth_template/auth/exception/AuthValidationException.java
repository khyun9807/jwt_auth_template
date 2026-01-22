package com.jwt_auth_template.auth.exception;

public class AuthValidationException extends RuntimeException {
    public AuthValidationException() {
        super();
    }

    public AuthValidationException(String message) {
        super(message);
    }

    public AuthValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
