package com.jwt_auth_template.auth;

import com.jwt_auth_template.auth.dto.JoinWithKakaoRequestDto;
import com.jwt_auth_template.auth.dto.OAuthMemberInfo;
import com.jwt_auth_template.exception.ErrorCode;
import com.jwt_auth_template.exception.JwtValidAuthenticationException;
import com.jwt_auth_template.exception.OAuthException;
import com.jwt_auth_template.exception.ReissueException;
import com.jwt_auth_template.jwt.JwtTokenUtil;
import com.jwt_auth_template.jwt.JwtType;
import com.jwt_auth_template.jwt.RefreshTokenEntity;
import com.jwt_auth_template.jwt.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

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

    public String enrollNewAuthTokens(String memberIdentifier, HttpServletResponse response, Date issuedAt) {
        String accessToken = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, issuedAt, memberIdentifier);
        String refreshToken = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, issuedAt, memberIdentifier);

        RefreshTokenEntity refreshTokenEntity =
                jwtTokenUtil.generateRefreshTokenEntity(memberIdentifier,refreshToken,issuedAt);

        jwtTokenUtil.upsertRefreshTokenEntity(refreshTokenEntity);
        jwtTokenUtil.generateCookieRefreshToken(refreshTokenEntity, response);

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

    public String reissueAccessToken(String refreshToken,HttpServletResponse response){
        try{
            String memberIdentifier = jwtTokenUtil.getMemberIdentifier(refreshToken);

            RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);

            return enrollNewAuthTokens(memberIdentifier,response,refreshTokenEntity.getIssuedAt());
        }
        catch(JwtValidAuthenticationException e){
            switch(e.getErrorCode()){
                case ErrorCode.JWT_ERROR -> {//높은 확률로 replay attack
                    

                    throw new ReissueException(ErrorCode.JWT_REISSUE_ERROR);
                }
                case ErrorCode.JWT_EXPIRED -> throw new ReissueException(ErrorCode.JWT_REISSUE_EXPIRED);
                default -> throw new ReissueException(ErrorCode.REISSUE_ERROR);
            }
        }
    }

    public void clearRefreshTokenAndRefreshTokenEntity(String refreshToken, HttpServletResponse response) {
        if(refreshToken!=null) {
            jwtTokenUtil.eraseCookieRefreshToken(response);
            jwtTokenUtil.deleteRefreshTokenEntity(refreshToken);
        }
    }
}
