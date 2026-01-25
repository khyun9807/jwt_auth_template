package com.jwt_auth_template.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberIdentifier(String memberIdentifier);

    @Query("update Member m set m.active = :active where m.id = :id")
    @Modifying
    int updateActiveById(boolean active, Long id);

    Optional<Member> findByEmailAndActive(String email, boolean active);

    Optional<Member> findByOauthIdAndActive(String oauthId, boolean active);

    Optional<Member> findByOauthIdAndNameAndAuthType(String oauthId, String name, AuthType authType);
}

