package com.koco.guardian.service;

import com.koco.guardian.config.CustomUserDetail;
import com.koco.guardian.entity.Authority;
import com.koco.guardian.entity.UserAuth;
import com.koco.guardian.repository.AuthorityRepository;
import com.koco.guardian.repository.UserAuthRepository;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserAuthRepository userAuthRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth userAuth = userAuthRepository.findByEmail(username)
                                          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<Authority> authorities =  userAuth.getRole().getAuthorities();
        if (Collections.isEmpty(authorities))
            throw new BadCredentialsException("No authorities present with user role : " + userAuth.getRole().getName());

        return new CustomUserDetail(userAuth, authorities);
    }
}
