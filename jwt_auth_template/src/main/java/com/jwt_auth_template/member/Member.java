package com.jwt_auth_template.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthType authType;

    private String oauthId;//OAuth2 외부 인증일때만 사용

    @Column(unique = true)
    private String email;//이메일 로그인일 때만 사용

    private String password;//이메일 로그인일 때만 사용

    public static Member createMember(
            boolean isActive,
            String name,
            MemberRole memberRole,
            AuthType authType,
            String oauthId,
            String password
    ) {
        Member member = new Member();
        member.setActive(isActive);
        member.setName(name);
        member.setMemberRole(memberRole);
        member.setAuthType(authType);
        member.setOauthId(oauthId);
        member.setPassword(password);
        return member;
    }
}
