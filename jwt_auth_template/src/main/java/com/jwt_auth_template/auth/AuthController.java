package com.jwt_auth_template.auth;

import com.jwt_auth_template.auth.dto.*;
import com.jwt_auth_template.exception.ApiResponse;
import com.jwt_auth_template.member.AuthType;
import com.jwt_auth_template.member.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/joinWithKakao")
    public ResponseEntity<ApiResponse<LoginSuccessResponseDto>> joinWithKakao(
            @RequestBody JoinWithKakaoRequestDto joinWithKakaoRequestDto,
            HttpServletResponse response
    ) {
        Member member = authService.joinWithKakao(joinWithKakaoRequestDto);

        String accessToken =
                authService.enrollNewAuthTokens(member, response);

        return ResponseEntity.ok(
                ApiResponse.ok(new LoginSuccessResponseDto(accessToken))
        );
    }


    @PostMapping("/loginWithKakao")
    public ResponseEntity<ApiResponse<LoginSuccessResponseDto>> login(
            @RequestBody LoginWithKakaoRequestDto loginWithKakaoRequestDto,
            HttpServletResponse response
    ) {
        Member member = authService.findMemberWithOauthToken(
                loginWithKakaoRequestDto.getOauthToken(),
                AuthType.KAKAO
        );

        String accessToken =
                authService.enrollNewAuthTokens(member, response);

        return ResponseEntity.ok(
                ApiResponse.ok(new LoginSuccessResponseDto(accessToken))
        );
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueSuccessResponseDto>> reissue(
            @CookieValue(required = false) String refreshToken,
            HttpServletResponse response
    ){
        String accessToken = authService.reissueAccessToken(refreshToken, response);

        return ResponseEntity.ok(
                ApiResponse.ok(new ReissueSuccessResponseDto(accessToken))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @CookieValue(required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.clearRefreshTokenAndEntity(refreshToken, response);

        return ResponseEntity.ok(ApiResponse.ok("logout success"));
    }
}
