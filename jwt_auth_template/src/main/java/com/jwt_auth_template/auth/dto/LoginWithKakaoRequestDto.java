package com.jwt_auth_template.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginWithKakaoRequestDto {
    private String oauthToken;
}
