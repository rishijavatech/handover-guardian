package com.koco.guardian.config;

import com.koco.guardian.entity.Authority;
import com.koco.guardian.entity.UserAuth;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class CustomUserDetail implements UserDetails {
    private UserAuth authUser;
    private List<Authority> authorities;

    public CustomUserDetail(UserAuth user, List<Authority> authorities) {
        this.authUser = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(authorities.size());
        for (Authority auth : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(auth.getAuthority().name()));
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return authUser.getPassword();
    }

    @Override
    public String getUsername() {
        return authUser.getEmail();
    }


}
