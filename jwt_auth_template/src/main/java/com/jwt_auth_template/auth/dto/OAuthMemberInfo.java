package com.jwt_auth_template.auth.dto;

import com.jwt_auth_template.member.AuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OAuthMemberInfo {
    private String name;

    private String oauthId;

    private AuthType authType;
}
