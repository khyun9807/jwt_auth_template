package com.jwt_auth_template.exception;

public class OAuthException extends ApiException{
    public OAuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
