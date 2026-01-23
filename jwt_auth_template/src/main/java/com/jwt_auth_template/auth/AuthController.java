package com.jwt_auth_template.auth;

import com.jwt_auth_template.auth.dto.JoinWithKakaoRequestDto;
import com.jwt_auth_template.auth.dto.LoginSuccessResponseDto;
import com.jwt_auth_template.auth.dto.LoginWithKakaoRequestDto;
import com.jwt_auth_template.member.AuthType;
import com.jwt_auth_template.member.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/joinWithKakao")
    public LoginSuccessResponseDto joinWithKakao(
            @RequestBody JoinWithKakaoRequestDto joinWithKakaoRequestDto,
            HttpServletResponse response
    ) {
        Member member = authService.joinWithKakao(joinWithKakaoRequestDto);

        String accessToken =
                authService.enrollAuthTokens(member.getId().toString(), response);

        return new LoginSuccessResponseDto(accessToken);
    }

    @PostMapping("/loginWithKakao")
    public LoginSuccessResponseDto login(
            @RequestBody LoginWithKakaoRequestDto loginWithKakaoRequestDto,
            HttpServletResponse response
    ) {
        Member member = authService.findMemberWithOauthToken(
                loginWithKakaoRequestDto.getOauthToken(),
                AuthType.KAKAO
        );

        String accessToken =
                authService.enrollAuthTokens(member.getId().toString(), response);

        return new LoginSuccessResponseDto(accessToken);
    }
}
