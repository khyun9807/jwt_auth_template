package com.jwt_auth_template.security;

import com.jwt_auth_template.member.Member;
import com.jwt_auth_template.member.MemberService;
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
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String memberIdentifier) throws UsernameNotFoundException {
        Member activeMember =
                memberService.getActiveMember(Long.parseLong(memberIdentifier));
        return new CustomUserDetails(activeMember);
    }
}
