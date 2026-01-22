package com.jwt_auth_template.security.jwt;

import com.jwt_auth_template.jwt.RefreshToken;
import com.jwt_auth_template.jwt.RefreshTokenRepository;
import com.jwt_auth_template.security.exception.JwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final JwtProperties jwtProperties=new JwtProperties();
    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        key=new SecretKeySpec(
                jwtProperties.getSecretKey()
                        .getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS512.key().build().getAlgorithm()
        );
    }

    public String generateJwtToken(JwtType jwtType,Date now,String memberIdentifier){
        Date expDate = new Date(
                now.getTime() +
                        (jwtType == JwtType.REFRESH ? jwtProperties.getRefreshTokenTime() : jwtProperties.getAccessTokenTime())
        );

        String token = Jwts.builder()
                .header()
                .type(jwtType.name())
                .and()
                .subject(memberIdentifier)
                .issuedAt(now)
                .expiration(expDate)
                .signWith(key)
                .compact();

        if(jwtType.equals(JwtType.REFRESH)){
            RefreshToken refreshToken = RefreshToken.createRefreshToken(
                    memberIdentifier,
                    token,
                    expDate
            );
            refreshTokenRepository.save(refreshToken);
        }

        return token;
    }

    public String extractJwtTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader("Authorization");

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return null;
    }

    public String getMemberIdentifier(String jwtToken){

        return getClaimsFromJwtToken(jwtToken)
                .getSubject();
    }

    private Claims getClaimsFromJwtToken(String jwtToken){
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtTokenException("Token has expired");
        } catch (UnsupportedJwtException e) {
            throw new JwtTokenException("Unsupported token");
        } catch (SignatureException e){
            throw new JwtTokenException("Token signature exception");
        }catch (MalformedJwtException e) {
            throw new JwtTokenException("Token is invalid");
        } catch (IllegalArgumentException e) {
            throw new JwtTokenException("Invalid JWT token");
        }
    }

    public JwtType getJwtType(String jwtToken){
        return JwtType.valueOf(Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwtToken)
                .getHeader().getType()
        );
    }
}
