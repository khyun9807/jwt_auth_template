package com.jwt_auth_template.member;

import com.jwt_auth_template.auth.dto.OAuthMemberInfo;
import com.jwt_auth_template.exception.ErrorCode;
import com.jwt_auth_template.exception.MemberException;
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
            throw new MemberException(ErrorCode.MEMBER_DUPLICATE);
        }
    }

    public Member getActiveMemberByMemberIdentifier(String memberIdentifier) {
        Member member = memberRepository.findByMemberIdentifier(memberIdentifier)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOTFOUND));

        if (!member.isActive()) {
            throw new MemberException(ErrorCode.MEMBER_NOTFOUND);
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
                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOTFOUND));

        if (!member.isActive()) {
            throw new MemberException(ErrorCode.MEMBER_NOTFOUND);
        }
        return member;
    }

    public void delete(final Member member) {
        memberRepository.updateActiveById(false, member.getId());
    }
}
