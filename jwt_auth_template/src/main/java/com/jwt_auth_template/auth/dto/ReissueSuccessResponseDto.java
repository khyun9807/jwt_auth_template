package com.jwt_auth_template.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ReissueSuccessResponseDto {

    private String accessToken;
}
