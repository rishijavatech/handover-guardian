package com.koco.guardian.service;

import com.koco.guardian.entity.UserRole;
import com.koco.guardian.repository.UserRoleRepository;
import com.koco.guardian.request.RoleCreateRequest;
import com.koco.guardian.response.RoleResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserRoleServiceTest {

    @Mock
    private UserRoleRepository roleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    public UserRoleServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRole_success() {
        RoleCreateRequest req = new RoleCreateRequest();
        req.setRole("Admin");
        when(roleRepository.existsByName("Admin")).thenReturn(false);

        RoleResponse resp = userRoleService.createRole(req);
        assertEquals("Role created successfully", resp.getMessage());
        assertEquals("Admin", resp.getRole());
        verify(roleRepository).save(any(UserRole.class));
    }

    @Test
    void createRole_alreadyExists() {
        RoleCreateRequest req = new RoleCreateRequest();
        req.setRole("Admin");
        when(roleRepository.existsByName("Admin")).thenReturn(true);

        RoleResponse resp = userRoleService.createRole(req);
        assertEquals("Role already exists", resp.getMessage());
        assertEquals("Admin", resp.getRole());
        verify(roleRepository, never()).save(any(UserRole.class));
    }

    @Test
    void getAllRoles_returnsList() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserRole> result = userRoleService.getAllRoles();
        assertEquals(0, result.size());
    }

    @Test
    void getRoleById_found() {
        UserRole role = new UserRole();
        role.setName("Admin");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        Optional<UserRole> result = userRoleService.getRoleById(1L);
        assertEquals("Admin", result.get().getName());
    }

    @Test
    void getRoleById_notFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<UserRole> result = userRoleService.getRoleById(1L);
        assertEquals(false, result.isPresent());
    }

    @Test
    void updateRole_success() {
        UserRole role = new UserRole();
        role.setName("Old");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(UserRole.class))).thenReturn(role);

        RoleCreateRequest req = new RoleCreateRequest();
        req.setRole("New");
        RoleResponse resp = userRoleService.updateRole(1L, req);
        assertEquals("Role updated successfully", resp.getMessage());
        assertEquals("New", resp.getRole());
    }

    @Test
    void updateRole_notFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        RoleCreateRequest req = new RoleCreateRequest();
        req.setRole("Admin");
        RoleResponse resp = userRoleService.updateRole(1L, req);
        assertEquals("Role not found", resp.getMessage());
        assertEquals("Admin", resp.getRole());
    }

    @Test
    void deleteRole_success() {
        UserRole role = new UserRole();
        role.setName("Admin");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleResponse resp = userRoleService.deleteRole(1L);
        assertEquals("Role deleted successfully", resp.getMessage());
        assertEquals("Admin", resp.getRole());
        verify(roleRepository).delete(role);
    }

    @Test
    void deleteRole_notFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        RoleResponse resp = userRoleService.deleteRole(1L);
        assertEquals("Role not found", resp.getMessage());
        assertEquals(null, resp.getRole());
    }
}
