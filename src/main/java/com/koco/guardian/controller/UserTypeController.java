package com.koco.guardian.controller;

import com.koco.guardian.entity.UserType;
import com.koco.guardian.request.UserTypeCreateRequest;
import com.koco.guardian.response.ApplicationResponse;
import com.koco.guardian.response.UserTypeResponse;
import com.koco.guardian.service.UserTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guardian/api/v1/user-type")
public class UserTypeController {

    private final UserTypeService userTypeService;

    @PostMapping
    public ResponseEntity<ApplicationResponse<UserTypeResponse>> createUserType(
            @Valid   @RequestBody  UserTypeCreateRequest request) {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(userTypeService.createUserType(request)));
    }

    @GetMapping
    public ResponseEntity<ApplicationResponse<List<UserType>>> getAllUserTypes() {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(userTypeService.getAllUserTypes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse<UserType>> getUserTypeById(@PathVariable Long id) {
        return userTypeService.getUserTypeById(id)
                              .map(type -> ResponseEntity.ok(ApplicationResponse.getSuccessResponse(type)))
                              .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse<UserTypeResponse>> updateUserType(@PathVariable Long id,
            @RequestBody @Valid UserTypeCreateRequest request) {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(userTypeService.updateUserType(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApplicationResponse<UserTypeResponse>> deleteUserType(@PathVariable Long id) {
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(userTypeService.deleteUserType(id)));
    }
}
