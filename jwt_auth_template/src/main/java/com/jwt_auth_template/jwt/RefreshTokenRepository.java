package com.jwt_auth_template.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    int deleteByMemberIdentifier(String memberIdentifier);

    RefreshTokenEntity findByRefreshToken(String refreshToken);
}
