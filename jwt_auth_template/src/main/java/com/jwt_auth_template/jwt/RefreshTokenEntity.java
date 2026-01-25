package com.jwt_auth_template.jwt;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="refresh_token_entity_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false,unique = true)
    private String memberIdentifier;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Date issuedAt;

    @Column(nullable = false)
    private Date expiresAt;

    public static RefreshTokenEntity createRefreshToken(
            String memberIdentifier,
            String refreshToken,
            Date issuedAt,
            Date expiresAt
    ) {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setMemberIdentifier(memberIdentifier);
        token.setRefreshToken(refreshToken);
        token.setIssuedAt(issuedAt);
        token.setExpiresAt(expiresAt);
        return token;
    }
}
