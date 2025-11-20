package com.koco.guardian.service;

import com.koco.guardian.entity.Authority;
import com.koco.guardian.entity.UserRole;
import com.koco.guardian.exception.KocopeException;
import com.koco.guardian.repository.AuthorityRepository;
import com.koco.guardian.repository.UserRoleRepository;
import com.koco.guardian.request.AuthorityCreateRequest;
import com.koco.guardian.response.AuthorityResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthorityServiceTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserRoleRepository roleRepository;

    @InjectMocks
    private AuthorityService authorityService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAuthority_Success() throws KocopeException {
        List<String> names = Arrays.asList("AUTH_READ", "AUTH_WRITE");
        AuthorityCreateRequest request = new AuthorityCreateRequest();
        request.setNames(names);

        when(authorityRepository.existsByName("AUTH_READ")).thenReturn(false);
        when(authorityRepository.existsByName("AUTH_WRITE")).thenReturn(false);

        AuthorityResponse response = authorityService.createAuthority(request);

        assertTrue(response.getAuthorities().containsAll(names));
        assertTrue(response.getMessage().contains("Authorities created"));
        verify(authorityRepository, times(2)).save(any(Authority.class));
    }

    @Test
    void testCreateAuthority_InvalidNames_ThrowsException() {
        List<String> names = Arrays.asList("validName", "Invalid Name!");
        AuthorityCreateRequest request = new AuthorityCreateRequest();
        request.setNames(names);

        KocopeException ex = assertThrows(KocopeException.class, () -> {
            authorityService.createAuthority(request);
        });
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("Invalid authority names"));
    }

    @Test
    void testGetAllAuthorities() {
        when(authorityRepository.findAll()).thenReturn(Collections.emptyList());
        List<Authority> authorities = authorityService.getAllAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testGetAuthorityById_Found() {
        Authority auth = new Authority();
        auth.setName("AUTH_TEST");
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(auth));

        Optional<Authority> result = authorityService.getAuthorityById(1L);
        assertTrue(result.isPresent());
        assertEquals("AUTH_TEST", result.get().getName());
    }

    @Test
    void testUpdateAuthority_Success() {
        Authority auth = new Authority();
        auth.setName("OLD_NAME");
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(auth));

        AuthorityCreateRequest request = new AuthorityCreateRequest();
        request.setNames(Collections.singletonList("NEW_NAME"));

        AuthorityResponse response = authorityService.updateAuthority(1L, request);
        assertEquals("Authority updated successfully", response.getMessage());
        assertEquals("NEW_NAME", response.getAuthorities().get(0));
        verify(authorityRepository).save(auth);
    }

    @Test
    void testUpdateAuthority_NotFound() {
        when(authorityRepository.findById(1L)).thenReturn(Optional.empty());
        AuthorityCreateRequest request = new AuthorityCreateRequest();
        request.setNames(Collections.singletonList("NEW_NAME"));

        AuthorityResponse response = authorityService.updateAuthority(1L, request);
        assertEquals("Authority not found", response.getMessage());
        assertNull(response.getAuthorities());
    }

    @Test
    void testDeleteAuthority_Success() {
        Authority auth = new Authority();
        auth.setName("AUTH_DELETE");
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(auth));

        AuthorityResponse response = authorityService.deleteAuthority(1L);
        assertEquals("Authority deleted successfully", response.getMessage());
        assertEquals("AUTH_DELETE", response.getAuthorities().get(0));
        verify(authorityRepository).delete(auth);
    }

    @Test
    void testDeleteAuthority_NotFound() {
        when(authorityRepository.findById(1L)).thenReturn(Optional.empty());

        AuthorityResponse response = authorityService.deleteAuthority(1L);
        assertEquals("Authority not found", response.getMessage());
        assertNull(response.getAuthorities());
    }

    @Test
    void testAssignAuthorityToRole_Success() {
        UserRole role = new UserRole();
        role.setName("Admin");
        role.setAuthorities(new HashSet<>());

        Authority authority = new Authority();
        authority.setName("AUTH_READ");

        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));
        when(authorityRepository.findByName("AUTH_READ")).thenReturn(Optional.of(authority));

        AuthorityResponse response = authorityService.assignAuthorityToRole("Admin", "AUTH_READ");
        assertEquals("Authority assigned successfully", response.getMessage());
        assertTrue(role.getAuthorities().contains(authority));
        verify(roleRepository).save(role);
    }

    @Test
    void testAssignAuthorityToRole_AlreadyAssigned() {
        UserRole role = new UserRole();
        role.setName("Admin");
        Authority authority = new Authority();
        authority.setName("AUTH_READ");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        role.setAuthorities(authorities);

        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));
        when(authorityRepository.findByName("AUTH_READ")).thenReturn(Optional.of(authority));

        AuthorityResponse response = authorityService.assignAuthorityToRole("Admin", "AUTH_READ");
        assertEquals("Authority already assigned to role", response.getMessage());
        assertTrue(response.getAuthorities().contains("AUTH_READ"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void testAssignAuthorityToRole_NotFound() {
        when(roleRepository.findByName("Admin")).thenReturn(Optional.empty());
        when(authorityRepository.findByName("AUTH_READ")).thenReturn(Optional.empty());

        AuthorityResponse response = authorityService.assignAuthorityToRole("Admin", "AUTH_READ");
        assertEquals("Role or Authority not found", response.getMessage());
        assertNull(response.getAuthorities());
    }

    @Test
    void testRevokeAuthorityFromRole_NotAssigned() {
        UserRole role = new UserRole();
        role.setName("Admin");
        role.setAuthorities(new HashSet<>());

        Authority authority = new Authority();
        authority.setName("AUTH_NOT_ASSIGNED");

        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));
        when(authorityRepository.findByName("AUTH_NOT_ASSIGNED")).thenReturn(Optional.of(authority));

        AuthorityResponse response = authorityService.revokeAuthorityFromRole("Admin", "AUTH_NOT_ASSIGNED");

        assertEquals("Authority not assigned to role", response.getMessage());
        assertTrue(response.getAuthorities().contains("AUTH_NOT_ASSIGNED"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void testRevokeAuthorityFromRole_NotFound() {
        when(roleRepository.findByName("Admin")).thenReturn(Optional.empty());
        when(authorityRepository.findByName("AUTH_ANY")).thenReturn(Optional.empty());

        AuthorityResponse response = authorityService.revokeAuthorityFromRole("Admin", "AUTH_ANY");

        assertEquals("Role or Authority not found", response.getMessage());
        assertNull(response.getAuthorities());
    }
    @Test
    void testGetAllAuthorityForRole_Success() {
        UserRole role = new UserRole();
        role.setName("Admin");
        Authority authority = new Authority();
        authority.setName("AUTH1");
        role.setAuthorities(new HashSet<>(Collections.singletonList(authority)));

        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));

        AuthorityResponse response = authorityService.getAllAuthorityForRole("Admin");

        assertEquals("Authorities fetched successfully", response.getMessage());
        assertTrue(response.getAuthorities().contains("AUTH1"));
    }

    @Test
    void testGetAllAuthorityForRole_NotFound() {
        when(roleRepository.findByName("NoRole")).thenReturn(Optional.empty());

        AuthorityResponse response = authorityService.getAllAuthorityForRole("NoRole");

        assertEquals("Role not found", response.getMessage());
        assertNull(response.getAuthorities());
    }

    @Test
    void testCreateAuthority_SaveThrowsException() {
        List<String> names = Arrays.asList("AUTH_X");
        AuthorityCreateRequest request = new AuthorityCreateRequest();
        request.setNames(names);
        when(authorityRepository.existsByName("AUTH_X")).thenReturn(false);
        when(authorityRepository.save(any(Authority.class))).thenThrow(new RuntimeException("DB fail"));

        assertThrows(RuntimeException.class, () -> {
            authorityService.createAuthority(request);
        });
    }
    @Test
    void testDeleteAuthority_Success_VerifyDeleteCalled() {
        Authority auth = new Authority();
        auth.setName("AUTH_DELETE");
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(auth));

        AuthorityResponse response = authorityService.deleteAuthority(1L);

        assertEquals("Authority deleted successfully", response.getMessage());
        assertEquals("AUTH_DELETE", response.getAuthorities().get(0));

        verify(authorityRepository).findById(1L);
        verify(authorityRepository).delete(auth);
        verifyNoMoreInteractions(authorityRepository);
    }
    @Test
    void testAssignAuthoritiesToRole_AllInvalidAuthorities() {
        UserRole role = new UserRole();
        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));
        when(authorityRepository.findByName(anyString())).thenReturn(Optional.empty());

        AuthorityResponse response = authorityService.assignAuthoritiesToRole("Admin", Arrays.asList("X", "Y"));
        assertEquals("No valid authorities found", response.getMessage());
        assertNull(response.getAuthorities());
    }

    @Test
    void testAssignAuthoritiesToRole_MixedValidInvalid() {
        UserRole role = new UserRole();
        role.setAuthorities(new HashSet<>());  // Initialize the authorities set here

        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));

        Authority validAuth = new Authority();
        validAuth.setName("VALID");
        when(authorityRepository.findByName("VALID")).thenReturn(Optional.of(validAuth));
        when(authorityRepository.findByName("INVALID")).thenReturn(Optional.empty());

        AuthorityResponse response = authorityService.assignAuthoritiesToRole("Admin", Arrays.asList("VALID", "INVALID"));

        assertTrue(response.getAuthorities().contains("VALID"));
        assertTrue(response.getMessage().contains("invalid"));
        verify(roleRepository).save(role);
    }


    @Test
    void testRevokeAuthorityFromRole_Success() {
        UserRole role = new UserRole();
        Authority auth = new Authority();
        auth.setName("AUTH");
        role.setAuthorities(new HashSet<>(Collections.singleton(auth)));
        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));
        when(authorityRepository.findByName("AUTH")).thenReturn(Optional.of(auth));

        AuthorityResponse response = authorityService.revokeAuthorityFromRole("Admin", "AUTH");

        assertEquals("Authority revoked successfully", response.getMessage());
        verify(roleRepository).save(role);
    }

    @Test
    void testGetAllAuthorityForRole_EmptyAuthorities() {
        UserRole role = new UserRole();
        role.setAuthorities(new HashSet<>());
        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));

        AuthorityResponse response = authorityService.getAllAuthorityForRole("Admin");

        assertEquals("Authorities fetched successfully", response.getMessage());
        assertTrue(response.getAuthorities().isEmpty());
    }



}
