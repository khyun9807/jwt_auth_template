package com.jwt_auth_template.test;


import com.jwt_auth_template.auth.KakaoOAuthUtil;
import com.jwt_auth_template.auth.dto.LoginWithKakaoRequestDto;
import com.jwt_auth_template.auth.dto.MeResponseDto;
import com.jwt_auth_template.auth.dto.OAuthMemberInfo;
import com.jwt_auth_template.member.AuthType;
import com.jwt_auth_template.member.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TestController {

    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @GetMapping("/me")
    public MeResponseDto getMe(
            Principal principal,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("principal.getName() = " + principal.getName());
        System.out.println("userDetails.username() = " + userDetails.getUsername());
        System.out.println("userDetails.password() = " + userDetails.getUsername());
        System.out.println("userDetails.getAuthorities() = " + userDetails.getAuthorities());
        
        return new MeResponseDto(
                "ex",
                AuthType.KAKAO,
                "ex",
                MemberRole.USER
        );
    }

    @GetMapping("/me/2")
    public String me2() {
        return "Hello Me";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Hello Admin";
    }

    @Autowired
    private KakaoOAuthUtil kakaoOAuthUtil;

    @GetMapping("/kakaoTest")
    public ResponseEntity<OAuthMemberInfo> kakaoTest(
            @RequestBody
            LoginWithKakaoRequestDto loginWithKakaoRequestDto
    ) {
        System.out.println("loginWithKakaoRequestDto.getOauthToken() = " + loginWithKakaoRequestDto.getOauthToken());
        return ResponseEntity.ok(kakaoOAuthUtil.getMemberInfoFromOAuthToken(loginWithKakaoRequestDto.getOauthToken()));
    }
}
