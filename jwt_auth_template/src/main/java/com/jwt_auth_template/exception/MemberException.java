package com.jwt_auth_template.exception;

public class MemberException extends ApiException{

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
