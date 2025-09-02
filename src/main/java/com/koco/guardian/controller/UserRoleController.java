package com.koco.guardian.controller;

import com.koco.guardian.entity.UserRole;
import com.koco.guardian.request.RoleCreateRequest;
import com.koco.guardian.response.ApplicationResponse;
import com.koco.guardian.response.RoleResponse;
import com.koco.guardian.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guardian/api/v1/role")
public class UserRoleController {

    private final UserRoleService roleService;

    @PostMapping
    public ResponseEntity<ApplicationResponse<RoleResponse>> createRole(@RequestBody RoleCreateRequest request) {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(roleService.createRole(request)));
    }

    @GetMapping
    public ResponseEntity<ApplicationResponse<List<UserRole>>> getAllRoles() {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(roleService.getAllRoles()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse<UserRole>> getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id)
                          .map(role -> ResponseEntity.ok(ApplicationResponse.getSuccessResponse(role)))
                          .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse<RoleResponse>> updateRole(@PathVariable Long id, @RequestBody RoleCreateRequest request) {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(roleService.updateRole(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApplicationResponse<RoleResponse>> deleteRole(@PathVariable Long id) {
        RoleResponse response = roleService.deleteRole(id);
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(response));
    }
}
