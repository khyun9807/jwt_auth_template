package com.jwt_auth_template.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    int deleteByMemberIdentifier(String memberIdentifier);

    RefreshTokenEntity findByRefreshToken(String refreshToken);
}
