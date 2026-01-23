package com.jwt_auth_template.member;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final RepositoryMethodInvocationListener repositoryMethodInvocationListener;

    public Member save(final Member member) {
        validateDuplicate(member);

        return memberRepository.save(member);
    }

    private void validateDuplicate(final Member member) {
        Member findMember = switch (member.getAuthType()) {
            case EMAIL ->
                    memberRepository.findByEmailAndActive(member.getEmail(),true);
            case KAKAO ->
                    memberRepository.findByOauthIdAndActive(member.getOauthId(),true);
            default -> null;
        };

        if(findMember != null) {
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

    public void delete(final Member member) {
        memberRepository.updateActiveById(false, member.getId());
    }
}
