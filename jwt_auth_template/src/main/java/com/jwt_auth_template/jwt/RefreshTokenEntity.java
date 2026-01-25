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

    @Column(nullable = false,unique = true,updatable = false)
    private String memberIdentifier;

    @Column(nullable = false)
    private String refreshToken;

    public static RefreshTokenEntity createRefreshToken(
            String memberIdentifier,
            String refreshToken
    ) {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setMemberIdentifier(memberIdentifier);
        token.setRefreshToken(refreshToken);

        return token;
    }
}
