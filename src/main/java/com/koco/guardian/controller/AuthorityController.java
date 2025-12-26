package com.koco.guardian.controller;

import com.koco.guardian.entity.Authority;
import com.koco.guardian.exception.KocopeException;
import com.koco.guardian.request.AuthorityCreateRequest;
import com.koco.guardian.response.ApplicationResponse;
import com.koco.guardian.response.AuthorityResponse;
import com.koco.guardian.service.AuthorityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guardian/api/v1/authority")
public class AuthorityController {

    private final AuthorityService authorityService;

    @PostMapping
    public ResponseEntity<ApplicationResponse<AuthorityResponse>> createAuthority(
            @RequestBody @Valid AuthorityCreateRequest request) throws
            KocopeException {
        AuthorityResponse response = authorityService.createAuthority(request);
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(response));
    }

    @GetMapping
    public ResponseEntity<ApplicationResponse<List<Authority>>> getAllAuthorities() {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(authorityService.getAllAuthorities()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse<Authority>> getAuthorityById(@PathVariable Long id) {
        return authorityService.getAuthorityById(id)
                               .map(auth -> ResponseEntity.ok(ApplicationResponse.getSuccessResponse(auth)))
                               .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all/{role}")
    public ResponseEntity<ApplicationResponse<AuthorityResponse>> getAllAuthorityForRole(@PathVariable String role) throws
            KocopeException {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(authorityService.getAllAuthorityForRole(role)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse<AuthorityResponse>> updateAuthority(@PathVariable Long id,
            @Valid @RequestBody AuthorityCreateRequest request) {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(authorityService.updateAuthority(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApplicationResponse<AuthorityResponse>> deleteAuthority(@PathVariable Long id) {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(authorityService.deleteAuthority(id)));
    }

    @PostMapping("/assign/{role}/{authority}")
    public ResponseEntity<ApplicationResponse<AuthorityResponse>> assignAuthorityToRole(
            @PathVariable String role, @PathVariable String authority) throws KocopeException {
        return ResponseEntity.ok(
                ApplicationResponse.getSuccessResponse(authorityService.assignAuthorityToRole(role, authority)));
    }

    @PostMapping("/assign-list/{role}")
    public ResponseEntity<ApplicationResponse<AuthorityResponse>> assignAuthoritiesToRole(
            @PathVariable String role, @RequestBody List<String> authorities) throws KocopeException {
        return ResponseEntity.ok(
                ApplicationResponse.getSuccessResponse(authorityService.assignAuthoritiesToRole(role, authorities)));
    }

    @DeleteMapping("/revoke/{role}/{authority}")
    public ResponseEntity<ApplicationResponse<AuthorityResponse>> revokeAuthorityFromRole(
            @PathVariable String role, @PathVariable String authority) {
        return ResponseEntity.ok(
                ApplicationResponse.getSuccessResponse(authorityService.revokeAuthorityFromRole(role, authority)));
    }
}
