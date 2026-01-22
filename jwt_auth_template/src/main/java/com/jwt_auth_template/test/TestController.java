package com.jwt_auth_template.test;


import com.jwt_auth_template.auth.KakaoOAuthUtil;
import com.jwt_auth_template.auth.dto.LoginWithKakaoRequestDto;
import com.jwt_auth_template.auth.dto.OAuthMemberInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @GetMapping("/auth/me")
    public String me() {
        return "Hello Me";
    }

    @Autowired
    private KakaoOAuthUtil kakaoOAuthUtil;

    @GetMapping("/kakaoTest")
    public ResponseEntity<OAuthMemberInfo> kakaoTest(
            @RequestBody
            LoginWithKakaoRequestDto loginWithKakaoRequestDto
    ) {
        System.out.println("loginWithKakaoRequestDto.getOAuthToken() = " + loginWithKakaoRequestDto.getOAuthToken());
        return ResponseEntity.ok(kakaoOAuthUtil.getMemberInfoFromOAuthToken(loginWithKakaoRequestDto.getOAuthToken()));
    }
}
