package com.jwt_auth_template.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinWithKakaoRequestDto {
    private String oauthToken;
}
