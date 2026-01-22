package com.jwt_auth_template.jwt;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="refresh_token_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    private String memberIdentifier;

    private String refreshToken;

    private LocalDateTime expiresAt;

    public static RefreshToken createRefreshToken(
            String memberIdentifier,
            String refreshToken,
            LocalDateTime expiresAt
    ) {
        RefreshToken token = new RefreshToken();
        token.setMemberIdentifier(memberIdentifier);
        token.setRefreshToken(refreshToken);
        token.setExpiresAt(expiresAt);
        return token;
    }
}
