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

    private boolean isActive;

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    private String password;

    public static Member createMember(
            boolean isActive,
            String name,
            MemberRole memberRole,
            AuthType authType,
            String password
    ) {
        Member member = new Member();
        member.setActive(isActive);
        member.setName(name);
        member.setRole(memberRole);
        member.setAuthType(authType);
        member.setPassword(password);
        return member;
    }
}
