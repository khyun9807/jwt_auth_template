package com.jwt_auth_template.security.jwt;

import com.jwt_auth_template.jwt.JwtTokenUtil;
import com.jwt_auth_template.jwt.JwtType;
import com.jwt_auth_template.jwt.RefreshTokenEntity;
import com.jwt_auth_template.jwt.RefreshTokenRepository;
import com.jwt_auth_template.security.exception.JwtTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        // HS512 테스트용(충분히 긴 secret)
        "jwt.secret-key=0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789ABCD",
        // access 만료를 짧게 해서 만료 케이스 빠르게
        "jwt.access-token-exptime=1s",
        "jwt.refresh-token-exptime=30s"
})
class JwtTokenUtilTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void accessToken_발급_검증_멤버식별자_및_타입확인() {
        // given
        Date now = new Date();
        String memberIdentifier = "member-123";

        // when
        String token = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, now, memberIdentifier);

        // then
        Assertions.assertThat(token).isNotBlank();
        Assertions.assertThat(jwtTokenUtil.getMemberIdentifier(token)).isEqualTo(memberIdentifier);
        Assertions.assertThat(jwtTokenUtil.getJwtType(token)).isEqualTo(JwtType.ACCESS);

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void refreshToken_발급시_DB에_저장된다() {
        // given
        Date now = new Date();
        String memberIdentifier = "member-999";

        // when
        String token = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, now, memberIdentifier);

        // then
        Assertions.assertThat(token).isNotBlank();
        verify(refreshTokenRepository, times(1)).save(any(RefreshTokenEntity.class));

        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshTokenEntity saved = captor.getValue();
        Assertions.assertThat(saved.getMemberIdentifier()).isEqualTo(memberIdentifier);
        Assertions.assertThat(saved.getRefreshToken()).isEqualTo(token);
        Assertions.assertThat(saved.getExpiresAt()).isNotNull();
    }

    @Test
    void 만료된_토큰이면_Expired_예외가_난다() {
        // access-token-exptime=1s
        // now를 2초 과거로 잡으면 exp가 과거가 됨
        Date pastNow = new Date(System.currentTimeMillis() - 2000);

        String token = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, pastNow, "member-expired");

        // when / then
        Assertions.assertThatThrownBy(() -> jwtTokenUtil.getMemberIdentifier(token))
                .isInstanceOf(JwtTokenException.class)
                .hasMessage("Token has expired");
    }

    @Test
    void 토큰을_변조하면_서명검증_예외가_난다() {
        String token = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, new Date(), "member-1");
        String tampered = token.substring(0, token.length() - 1) + "x";

        Assertions.assertThatThrownBy(() -> jwtTokenUtil.getMemberIdentifier(tampered))
                .isInstanceOf(JwtTokenException.class)
                .hasMessage("Token signature exception");
    }

    @Test
    void 형식이_깨진_토큰이면_malformed_예외가_난다() {
        // '.' 구분이 없어서 JJWT 파서가 보통 Malformed로 처리
        String malformed = "this-is-not-a-jwt";

        Assertions.assertThatThrownBy(() -> jwtTokenUtil.getMemberIdentifier(malformed))
                .isInstanceOf(JwtTokenException.class)
                // 네 코드에선 MalformedJwtException -> "Token is invalid"
                .hasMessage("Token is invalid");
    }

    @Test
    void null_토큰이면_invalid_예외가_난다() {
        Assertions.assertThatThrownBy(() -> jwtTokenUtil.getMemberIdentifier(null))
                .isInstanceOf(JwtTokenException.class)
                .hasMessage("Invalid JWT token");
    }

    @Test
    void getJwtType도_정상적으로_타입을_읽는다() {
        String access = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, new Date(), "m1");
        String refresh = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, new Date(), "m1");

        Assertions.assertThat(jwtTokenUtil.getJwtType(access)).isEqualTo(JwtType.ACCESS);
        Assertions.assertThat(jwtTokenUtil.getJwtType(refresh)).isEqualTo(JwtType.REFRESH);
    }

    @Test
    void Authorization_Bearer_헤더에서_토큰을_추출한다() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");

        String extracted = jwtTokenUtil.extractJwtTokenFromRequest(req);

        Assertions.assertThat(extracted).isEqualTo("abc.def.ghi");
    }

    @Test
    void Authorization_헤더가_없거나_Bearer가_아니면_null() {
        HttpServletRequest req1 = mock(HttpServletRequest.class);
        when(req1.getHeader("Authorization")).thenReturn(null);
        Assertions.assertThat(jwtTokenUtil.extractJwtTokenFromRequest(req1)).isNull();

        HttpServletRequest req2 = mock(HttpServletRequest.class);
        when(req2.getHeader("Authorization")).thenReturn("");
        Assertions.assertThat(jwtTokenUtil.extractJwtTokenFromRequest(req2)).isNull();

        HttpServletRequest req3 = mock(HttpServletRequest.class);
        when(req3.getHeader("Authorization")).thenReturn("Basic abc");
        Assertions.assertThat(jwtTokenUtil.extractJwtTokenFromRequest(req3)).isNull();

        HttpServletRequest req4 = mock(HttpServletRequest.class);
        when(req4.getHeader("Authorization")).thenReturn("Bearer ");

        Assertions.assertThat(jwtTokenUtil.extractJwtTokenFromRequest(req4)).isNull();
    }
}