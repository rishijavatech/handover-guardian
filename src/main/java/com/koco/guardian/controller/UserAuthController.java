package com.koco.guardian.controller;

import com.koco.guardian.exception.KocopeException;
import com.koco.guardian.request.UserLoginRequest;
import com.koco.guardian.request.UserRegisterRequest;
import com.koco.guardian.response.ApplicationResponse;
import com.koco.guardian.response.UserAuthResponse;
import com.koco.guardian.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/guardian/api/v1/users")
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<ApplicationResponse<UserAuthResponse>> registerUser(
            @RequestBody @Valid UserRegisterRequest userRegisterRequest) throws
            KocopeException {
        log.info("Received request to register user with email: {}", userRegisterRequest.getEmail());
        return ResponseEntity.ok(
                ApplicationResponse.getSuccessResponse(userAuthService.registerUser(userRegisterRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApplicationResponse<UserAuthResponse>> loginUser(
            @RequestBody @Valid UserLoginRequest userLoginRequest) {
        log.info("Received request to login user with email: {}", userLoginRequest.getEmail());
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(userAuthService.loginUser(userLoginRequest)));
    }

    @PatchMapping("/delete/{userId}")
    public ResponseEntity<ApplicationResponse<UserAuthResponse>> deleteUser(
            @PathVariable String userId) throws
            KocopeException {
        log.info("Received request for deleting user: {}", userId);
        return ResponseEntity.ok(
                ApplicationResponse.getSuccessResponse(userAuthService.deleteUser(userId)));

    }
}
