package com.koco.guardian.service;

import com.koco.guardian.entity.Authority;
import com.koco.guardian.entity.UserRole;
import com.koco.guardian.repository.AuthorityRepository;
import com.koco.guardian.repository.UserRoleRepository;
import com.koco.guardian.request.AuthorityCreateRequest;
import com.koco.guardian.response.AuthorityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final UserRoleRepository roleRepository;

    public AuthorityResponse createAuthority(AuthorityCreateRequest request) {
        List<String> existingAuthorities = new ArrayList<>();
        List<String> createdAuthorities = new ArrayList<>();
        for (String authorityName : request.getNames()) {
            if (authorityRepository.existsByName(authorityName)) {
                existingAuthorities.add(authorityName);
            } else {
                Authority authority = new Authority();
                authority.setName(authorityName);
                authorityRepository.save(authority);
                createdAuthorities.add(authorityName);
            }
        }
        String message = "Authorities created: " + String.join(", ", createdAuthorities)
                + ". Authorities already existed: " + String.join(", ", existingAuthorities);

        return new AuthorityResponse(createdAuthorities, message);
    }

    public List<Authority> getAllAuthorities() {
        return authorityRepository.findAll();
    }

    public Optional<Authority> getAuthorityById(Long id) {
        return authorityRepository.findById(id);
    }

    public AuthorityResponse updateAuthority(Long id, AuthorityCreateRequest request) {
        return authorityRepository.findById(id).map(auth -> {
            auth.setName(request.getNames().getFirst());
            authorityRepository.save(auth);
            return new AuthorityResponse(Collections.singletonList(request.getNames().getFirst()), "Authority updated successfully");
        }).orElse(new AuthorityResponse(null, "Authority not found"));
    }

    public AuthorityResponse deleteAuthority(Long id) {
        return authorityRepository.findById(id).map(auth -> {
            authorityRepository.delete(auth);
            return new AuthorityResponse(Collections.singletonList(auth.getName()), "Authority deleted successfully");
        }).orElse(new AuthorityResponse(null, "Authority not found"));
    }

    public AuthorityResponse assignAuthorityToRole(String role, String authority) {
        Optional<UserRole> roleOpt = roleRepository.findByName(role);
        Optional<Authority> authOpt = authorityRepository.findByName(authority);

        if (roleOpt.isPresent() && authOpt.isPresent()) {
            UserRole userRole = roleOpt.get();
            if(userRole.getAuthorities().contains(authOpt.get())) {
                return new AuthorityResponse(Collections.singletonList(authority), "Authority already assigned to " +
                        "role");
            }
            userRole.getAuthorities().add(authOpt.get());
            roleRepository.save(userRole);
            return new AuthorityResponse(Collections.singletonList(authority), "Authority assigned successfully");
        }
        return new AuthorityResponse(null, "Role or Authority not found");
    }

    public AuthorityResponse assignAuthoritiesToRole(String roleName, List<String> authorityNames) {
        Optional<UserRole> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            return new AuthorityResponse(null, "Role not found");
        }

        UserRole role = roleOpt.get();
        List<String> invalidAuthorities = new ArrayList<>();
        List<Authority> validAuthorities = new ArrayList<>();

        for (String authorityName : authorityNames) {
            Optional<Authority> authorityOpt = authorityRepository.findByName(authorityName);
            if (authorityOpt.isPresent()) {
                validAuthorities.add(authorityOpt.get());
            } else {
                invalidAuthorities.add(authorityName);
            }
        }

        if (validAuthorities.isEmpty()) {
            return new AuthorityResponse(null, "No valid authorities found");
        }

        role.getAuthorities().addAll(validAuthorities);
        roleRepository.save(role);

        // Prepare response message
        String message = "Authorities assigned successfully";
        if (!invalidAuthorities.isEmpty()) {
            message += ". The following authorities were invalid: " + String.join(", ", invalidAuthorities);
        }

        return new AuthorityResponse(validAuthorities.stream().map(Authority::getName).collect(Collectors.toList()), message);
    }

    public AuthorityResponse revokeAuthorityFromRole(String role, String authority) {
        Optional<UserRole> roleOpt = roleRepository.findByName(role);
        Optional<Authority> authOpt = authorityRepository.findByName(authority);

        if (roleOpt.isPresent() && authOpt.isPresent()) {
            UserRole userRole = roleOpt.get();
            if (userRole.getAuthorities().remove(authOpt.get())) {
                roleRepository.save(userRole);
                return new AuthorityResponse(Collections.singletonList(authority), "Authority revoked successfully");
            }
            return new AuthorityResponse(Collections.singletonList(authority), "Authority not assigned to role");
        }
        return new AuthorityResponse(null, "Role or Authority not found");
    }

    public AuthorityResponse getAllAuthorityForRole(String role) {
       return roleRepository.findByName(role).map(userRole -> {
            return new AuthorityResponse(userRole.getAuthorities().stream().map(Authority::getName).toList(),
                    "Authorities " +
                    "fetched successfully");
        }).orElse( new AuthorityResponse(null, "Role not found"));
    }
}
