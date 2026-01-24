package com.jwt_auth_template.auth;

import com.jwt_auth_template.auth.dto.JoinWithKakaoRequestDto;
import com.jwt_auth_template.auth.dto.OAuthMemberInfo;
import com.jwt_auth_template.exception.ErrorCode;
import com.jwt_auth_template.exception.OAuthException;
import com.jwt_auth_template.jwt.JwtTokenUtil;
import com.jwt_auth_template.jwt.JwtType;
import com.jwt_auth_template.jwt.RefreshTokenEntity;
import com.jwt_auth_template.member.AuthType;
import com.jwt_auth_template.member.Member;
import com.jwt_auth_template.member.MemberRole;
import com.jwt_auth_template.member.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberService memberService;
    private final KakaoOAuthUtil kakaoOAuthUtil;
    private final JwtTokenUtil jwtTokenUtil;

    public Member joinWithKakao(JoinWithKakaoRequestDto joinWithKakaoRequestDto) {

        String oauthToken = joinWithKakaoRequestDto.getOauthToken();
        OAuthMemberInfo oauthMemberInfo = kakaoOAuthUtil.getMemberInfoFromOAuthToken(oauthToken);

        Member member = Member.createMember(
                true,
                oauthMemberInfo.getName(),
                MemberRole.USER,
                oauthMemberInfo.getAuthType(),
                oauthMemberInfo.getOauthId(),
                null
        );
        return memberService.save(member);
    }

    public String enrollAuthTokens(String memberIdentifier, HttpServletResponse response) {
        Date issuedAt = new Date();
        String accessToken = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, issuedAt, memberIdentifier);
        String refreshToken = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, issuedAt, memberIdentifier);

        RefreshTokenEntity refreshTokenEntity =
                jwtTokenUtil.generateRefreshTokenEntity(
                        memberIdentifier,
                        refreshToken,
                        issuedAt
                );
        jwtTokenUtil.saveRefreshTokenEntity(refreshTokenEntity);
        jwtTokenUtil.setCookieRefreshToken(refreshTokenEntity, response);

        return accessToken;
    }

    public Member findMemberWithOauthToken(String oauthToken, AuthType authType) {

        OAuthMemberInfo oauthMemberInfo=switch (authType){
            case AuthType.KAKAO -> kakaoOAuthUtil.getMemberInfoFromOAuthToken(oauthToken);
            case AuthType.NAVER -> null;
            default -> null;
        };

        if(oauthMemberInfo==null) {
            throw new OAuthException(ErrorCode.OAUTH_RESOURCE_ERROR);
        }

        return memberService.getActiveOAuthMember(oauthMemberInfo);
    }
}
