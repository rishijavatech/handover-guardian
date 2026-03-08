package com.koco.guardian.service;

import com.koco.guardian.entity.UserRole;
import com.koco.guardian.repository.UserRoleRepository;
import com.koco.guardian.request.RoleCreateRequest;
import com.koco.guardian.response.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository roleRepository;

    public RoleResponse createRole(RoleCreateRequest request) {
        if (roleRepository.existsByName(request.getRole())) {
            return new RoleResponse(request.getRole(), "Role already exists");
        }
        UserRole role = new UserRole();
        role.setName(request.getRole());
        roleRepository.save(role);
        return new RoleResponse(role.getName(), "Role created successfully");
    }

    public List<UserRole> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<UserRole> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public RoleResponse updateRole(Long id, RoleCreateRequest request) {
        return roleRepository.findById(id).map(role -> {
            role.setName(request.getRole());
            roleRepository.save(role);
            return new RoleResponse(role.getName(), "Role updated successfully");
        }).orElse(new RoleResponse(request.getRole(), "Role not found"));
    }

    public RoleResponse deleteRole(Long id) {
        return roleRepository.findById(id).map(role -> {
            roleRepository.delete(role);
            return new RoleResponse(role.getName(), "Role deleted successfully");
        }).orElse(new RoleResponse(null, "Role not found"));
    }
}
