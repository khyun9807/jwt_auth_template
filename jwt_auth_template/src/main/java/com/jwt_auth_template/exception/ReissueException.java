package com.jwt_auth_template.exception;

public class ReissueException extends ApiException{
    public ReissueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
