package com.jwt_auth_template.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("update Member m set m.isActive = :active where m.id = :id")
    @Modifying
    int updateActiveById(boolean active, Long id);

    Member findByEmailAndActive(String email, boolean active);

    Member findByOauthIdAndActive(String oauthId, boolean active);
}

