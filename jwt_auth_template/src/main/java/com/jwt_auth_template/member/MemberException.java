package com.jwt_auth_template.member;

public class MemberException extends IllegalStateException{
    public MemberException(String s) {
        super(s);
    }

    public MemberException(String message, Throwable cause) {
        super(message, cause);
    }
}
