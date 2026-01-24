package com.jwt_auth_template.auth;

import com.jwt_auth_template.auth.dto.OAuthMemberInfo;
import com.jwt_auth_template.exception.OAuthException;
import com.jwt_auth_template.member.AuthType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(KakaoOAuthUtil.class)
class KakaoOAuthUtilTest {

    private static final String PROPERTY_KEYS = "[\"kakao_account.profile\"]";

    @Autowired KakaoOAuthUtil kakaoOAuthUtil;
    @Autowired MockRestServiceServer server;

    @AfterEach
    void tearDown() {
        server.verify();
    }

    private String expectedUserMeUri() {
        // ✅ RestClient가 만드는 실제 URI와 동일하게(인코딩 포함) 생성
        return UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .queryParam("property_keys", PROPERTY_KEYS)
                .build()
                .encode()
                .toUriString();
    }

    @Test
    void 토큰으로_회원정보를_정상조회한다() {
        String token = "test-oauth-token";
        String kakaoResponseJson = """
            {
              "id": 123456789,
              "kakao_account": { "profile": { "nickname": "kkh" } }
            }
            """;

        server.expect(requestTo(expectedUserMeUri()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andRespond(withSuccess(kakaoResponseJson, MediaType.APPLICATION_JSON));

        OAuthMemberInfo actual = kakaoOAuthUtil.getMemberInfoFromOAuthToken(token);

        OAuthMemberInfo expected = new OAuthMemberInfo("kkh", "123456789", AuthType.KAKAO);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void 카카오_API가_에러응답이면_OAuthException을_던진다() {
        String token = "bad-token";

        server.expect(requestTo(expectedUserMeUri()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"msg\":\"Unauthorized\"}"));

        assertThatThrownBy(() -> kakaoOAuthUtil.getMemberInfoFromOAuthToken(token))
                .isInstanceOf(OAuthException.class)
                .hasMessageContaining("Kakao API error");
    }

    @Test
    void 응답_JSON이_깨져있으면_OAuthException을_던진다() {
        String token = "test-token";

        server.expect(requestTo(expectedUserMeUri()))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andRespond(withSuccess("NOT_A_JSON", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> kakaoOAuthUtil.getMemberInfoFromOAuthToken(token))
                .isInstanceOf(OAuthException.class)
                .hasMessageContaining("Failed to parse Kakao response");
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        RestClient kakaoRestClient(RestClient.Builder builder) {
            return builder
                    .baseUrl("https://kapi.kakao.com")
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
