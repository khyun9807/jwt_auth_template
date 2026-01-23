package com.jwt_auth_template.member;

import com.jwt_auth_template.auth.dto.OAuthMemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public Member save(final Member member) {
        validateDuplicate(member);

        return memberRepository.save(member);
    }

    private void validateDuplicate(final Member member) {
        Optional<Member> findMember = switch (member.getAuthType()) {
            case EMAIL -> memberRepository.findByEmailAndActive(member.getEmail(), true);
            case KAKAO -> memberRepository.findByOauthIdAndActive(member.getOauthId(), true);
            default -> Optional.empty();
        };

        if (findMember.isPresent()) {
            throw new MemberException("member already exists");
        }
    }

    public Member getActiveMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException("member not found with id: " + id));

        if (!member.isActive()) {
            throw new MemberException("member inactivated");
        }
        return member;
    }

    public Member getActiveOAuthMember(OAuthMemberInfo oAuthMemberInfo) {
        Member member =
                memberRepository.findByOauthIdAndNameAndAuthType(
                                oAuthMemberInfo.getOauthId(),
                                oAuthMemberInfo.getName(),
                                oAuthMemberInfo.getAuthType()
                        )
                        .orElseThrow(() -> new MemberException("oauth member not found"));

        if (!member.isActive()) {
            throw new MemberException("oauth member inactivated");
        }
        return member;
    }

    public void delete(final Member member) {
        memberRepository.updateActiveById(false, member.getId());
    }
}
