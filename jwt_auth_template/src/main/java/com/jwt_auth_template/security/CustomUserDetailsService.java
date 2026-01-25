package com.jwt_auth_template.security;

import com.jwt_auth_template.exception.ErrorCode;
import com.jwt_auth_template.exception.MemberAuthenticationException;
import com.jwt_auth_template.member.Member;
import com.jwt_auth_template.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberIdentifier) throws UsernameNotFoundException {
        Member activeMember = getActiveMemberByMemberIdentifier(memberIdentifier);
        return new CustomUserDetails(activeMember);
    }

    public Member getActiveMemberByMemberIdentifier(String memberIdentifier) {
        Member member = memberRepository.findByMemberIdentifier(memberIdentifier)
                .orElseThrow(() -> new MemberAuthenticationException(ErrorCode.MEMBER_NOTFOUND));

        if (!member.isActive()) {
            throw new MemberAuthenticationException(ErrorCode.MEMBER_NOTFOUND);
        }
        return member;
    }
}
