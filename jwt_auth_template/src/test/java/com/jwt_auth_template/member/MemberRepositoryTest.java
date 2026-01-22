package com.jwt_auth_template.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save() {
        Member member = Member.createMember(
                true,
                "armstrong",
                MemberRole.USER,
                AuthType.KAKAO,
                null
        );

        memberRepository.save(member);

        Assertions.assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    void findById() {
        Member member = Member.createMember(
                true,
                "armstrong",
                MemberRole.USER,
                AuthType.KAKAO,
                null
        );
        memberRepository.save(member);

        Optional<Member> findMember = memberRepository.findById(member.getId());

        Assertions.assertThat(findMember).isPresent();
        Assertions.assertThat(findMember.get()).isEqualTo(member);
    }

    @Test
    void deleteById() {
        Member member = Member.createMember(
                true,
                "armstrong",
                MemberRole.USER,
                AuthType.KAKAO,
                null
        );
        memberRepository.save(member);

        int count =
                memberRepository.updateActiveById(false, member.getId());

        Assertions.assertThat(count).isEqualTo(1);
    }
}