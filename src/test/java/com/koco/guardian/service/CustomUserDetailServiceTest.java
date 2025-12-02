package com.koco.guardian.service;

import com.koco.guardian.entity.Authority;
import com.koco.guardian.entity.UserAuth;
import com.koco.guardian.entity.UserRole;
import com.koco.guardian.repository.UserAuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomUserDetailServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_success() {
        String email = "test@example.com";
        UserAuth userAuth = new UserAuth();
        UserRole role = new UserRole();
        Set<Authority> authorities = new HashSet<>();
        Authority auth = new Authority();
        auth.setName("READ_PRIVILEGE");
        authorities.add(auth);

        role.setAuthorities(authorities);
        userAuth.setRole(role);
        userAuth.setEmail(email);

        when(userAuthRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(userAuth));

        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
        assertNotNull(userDetails);

        // Verify username corresponds to userAuth's email
        assertEquals(email, userDetails.getUsername());

        // Verify authorities match
        assertFalse(userDetails.getAuthorities().isEmpty());

        verify(userAuthRepository, times(1)).findByEmailAndIsDeletedFalse(email);
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        String email = "notfound@example.com";
        when(userAuthRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailService.loadUserByUsername(email));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void loadUserByUsername_noAuthorities_throwsBadCredentialsException() {
        String email = "emptyroles@example.com";
        UserAuth userAuth = new UserAuth();
        UserRole role = new UserRole();
        role.setAuthorities(Collections.emptySet());
        userAuth.setRole(role);
        userAuth.setEmail(email);

        when(userAuthRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(userAuth));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                customUserDetailService.loadUserByUsername(email));
        assertTrue(exception.getMessage().contains("No authorities present with user role"));
    }
}
