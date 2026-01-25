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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

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
                .id(UUID.randomUUID().toString())
                .compact();
    }

    public RefreshTokenEntity generateRefreshTokenEntity(
            String memberIdentifier, String refreshToken
    ) {

        return RefreshTokenEntity.createRefreshToken(
                memberIdentifier,
                refreshToken
        );
    }


    //@Transactional
    public void upsertRefreshTokenEntity(RefreshTokenEntity refreshTokenEntity) {
        deleteAllRefreshTokenEntity(refreshTokenEntity.getMemberIdentifier());
        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteAllRefreshTokenEntity(String memberIdentifier) {
        int count = refreshTokenRepository.deleteByMemberIdentifier(memberIdentifier);
        refreshTokenRepository.flush();
        return count;
    }

    public RefreshTokenEntity getRefreshTokenEntity(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    public void generateCookieRefreshToken(RefreshTokenEntity refreshTokenEntity, HttpServletResponse response) {
        String refreshToken = refreshTokenEntity.getRefreshToken();
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        int age = (int) ((new Date()).getTime() - getExpiresAt(refreshToken).getTime() / 1000);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    public void eraseCookieRefreshToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
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

    public Date getIssuedAt(String jwtToken) {
        return getClaimsFromJwtToken(jwtToken)
                .getIssuedAt();
    }

    public Date getExpiresAt(String jwtToken) {
        return getClaimsFromJwtToken(jwtToken)
                .getExpiration();
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
        } catch (UnsupportedJwtException | SignatureException |
                 MalformedJwtException | IllegalArgumentException e) {
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
        } catch (UnsupportedJwtException | SignatureException |
                 MalformedJwtException | IllegalArgumentException e) {
            throw new JwtValidAuthenticationException(ErrorCode.JWT_ERROR);
        }
    }
}
