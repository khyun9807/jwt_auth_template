package com.jwt_auth_template.jwt;

import com.jwt_auth_template.exception.ErrorCode;
import com.jwt_auth_template.exception.JwtValidAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        key = new SecretKeySpec(
                jwtProperties.getSecretKey()
                        .getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS512.key().build().getAlgorithm()
        );
    }

    public String generateJwtToken(JwtType jwtType, Date now, String memberIdentifier) {
        Date expDate = new Date(
                now.getTime() +
                        (jwtType == JwtType.REFRESH ? jwtProperties.getRefreshTokenTime() : jwtProperties.getAccessTokenTime())
        );

        return Jwts.builder()
                .header()
                .type(jwtType.name())
                .and()
                .subject(memberIdentifier)
                .issuedAt(now)
                .expiration(expDate)
                .signWith(key)
                .compact();
    }

    public RefreshTokenEntity generateRefreshTokenEntity(
            String memberIdentifier, String token, Date now
    ) {
        Date expDate = new Date(
                now.getTime() +
                        jwtProperties.getRefreshTokenTime());

        return RefreshTokenEntity.createRefreshToken(
                memberIdentifier,
                token,
                expDate
        );
    }

    //@Transactional
    public void saveRefreshTokenEntity(RefreshTokenEntity refreshTokenEntity) {
        //refreshTokenRepository.deleteByMemberIdentifier(refreshTokenEntity.getMemberIdentifier());
        //refreshTokenRepository.flush();
        refreshTokenRepository.save(refreshTokenEntity);
    }

    public void setCookieRefreshToken(RefreshTokenEntity refreshTokenEntity, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshTokenEntity.getRefreshToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        int age = (int) ((new Date()).getTime() - refreshTokenEntity.getExpiresAt().getTime() / 1000);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    public String extractJwtTokenFromRequest(HttpServletRequest request) {
        String headerValue = request.getHeader("Authorization");

        if (StringUtils.hasText(headerValue) && headerValue.startsWith("Bearer ")) {
            String token = headerValue.substring(7);

            if (token.isEmpty())
                return null;

            return token;
        }

        return null;
    }

    public String getMemberIdentifier(String jwtToken) {

        return getClaimsFromJwtToken(jwtToken)
                .getSubject();
    }

    private Claims getClaimsFromJwtToken(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtValidAuthenticationException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException|SignatureException|
                 MalformedJwtException|IllegalArgumentException e) {
            throw new JwtValidAuthenticationException(ErrorCode.JWT_ERROR);
        }
    }

    public JwtType getJwtType(String jwtToken) {
        try {
            return JwtType.valueOf(Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getHeader().getType()
            );
        } catch (ExpiredJwtException e) {
            throw new JwtValidAuthenticationException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException|SignatureException|
                 MalformedJwtException|IllegalArgumentException e) {
            throw new JwtValidAuthenticationException(ErrorCode.JWT_ERROR);
        }
    }
}
