package com.jwt_auth_template.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@SpringBootTest
@Transactional
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void setAndGetRefreshToken() {
        RefreshTokenEntity refreshTokenEntity = jwtTokenUtil.generateRefreshTokenEntity(
                "323",
                "afsdasdf",
                new Date()
        );
        refreshTokenRepository.save(refreshTokenEntity);

        Optional<RefreshTokenEntity> findRefreshToken =
                refreshTokenRepository
                        .findById(refreshTokenEntity.getId());

        Assertions.assertThat(findRefreshToken).isPresent();
        Assertions.assertThat(findRefreshToken.get())
                .isEqualTo(refreshTokenEntity);
    }

}