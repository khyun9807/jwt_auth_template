package com.jwt_auth_template.jwt;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="refresh_token_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false,unique = true)
    private String memberIdentifier;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Date expiresAt;

    public static RefreshToken createRefreshToken(
            String memberIdentifier,
            String refreshToken,
            Date expiresAt
    ) {
        RefreshToken token = new RefreshToken();
        token.setMemberIdentifier(memberIdentifier);
        token.setRefreshToken(refreshToken);
        token.setExpiresAt(expiresAt);
        return token;
    }
}
