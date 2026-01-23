package com.jwt_auth_template.auth.dto;

import com.jwt_auth_template.member.AuthType;
import com.jwt_auth_template.member.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MeResponseDto {
    private String name;

    private AuthType authType;

    private String memberIdentifier;

    private MemberRole memberRole;
}
