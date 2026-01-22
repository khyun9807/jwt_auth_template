package com.jwt_auth_template.auth;

import com.jwt_auth_template.auth.dto.OAuthMemberInfo;
import com.jwt_auth_template.auth.exception.OAuthException;
import com.jwt_auth_template.member.AuthType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class KakaoOAuthUtil {
    private static final String PROPERTY_KEYS = "[\"kakao_account.profile\"]";

    private final RestClient kakaoRestClient;
    private final ObjectMapper objectMapper;

    public OAuthMemberInfo getMemberInfoFromOAuthToken(String oAuthToken) {
        String body = kakaoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/user/me")
                        .queryParam("property_keys", PROPERTY_KEYS)
                        .build())
                .headers(headers -> headers.setBearerAuth(oAuthToken))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (request, response) ->
                        {
                            throw new OAuthException("Kakao API error: " + response.getStatusCode());
                        }
                )
                .body(String.class);

        try {
            JsonNode json = objectMapper.readTree(body);

            String name = json.path("kakao_account")
                    .path("profile")
                    .path("nickname")
                    .asString();

            String kakaoId = json.path("id").asString();

            return new OAuthMemberInfo(name, kakaoId, AuthType.KAKAO);
        } catch (Exception e) {
            throw new OAuthException("Failed to parse Kakao response", e);
        }
    }
}
