package com.jwt_auth_template.auth.exception;

public class OAuthException extends RuntimeException{
    public OAuthException() {
        super();
    }

    public OAuthException(String message) {
        super(message);
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
