package com.jwt_auth_template.jwt;

import com.jwt_auth_template.exception.ErrorCode;
import com.jwt_auth_template.exception.JwtValidAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = (String) authentication.getCredentials();

        if (jwtTokenUtil.getJwtType(accessToken) != JwtType.ACCESS) {
            throw new JwtValidAuthenticationException(ErrorCode.JWT_ERROR);
        }

        String memberIdentifier = jwtTokenUtil.getMemberIdentifier(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberIdentifier);

        return UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
