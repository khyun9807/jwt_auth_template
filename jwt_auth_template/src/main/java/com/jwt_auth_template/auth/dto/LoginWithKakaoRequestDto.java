package com.jwt_auth_template.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginWithKakaoRequestDto {
    private String oAuthToken;
}
