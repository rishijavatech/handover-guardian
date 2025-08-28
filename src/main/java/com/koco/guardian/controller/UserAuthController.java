package com.koco.guardian.controller;

import com.koco.guardian.exception.KocopeException;
import com.koco.guardian.request.UserLoginRequest;
import com.koco.guardian.request.UserRegisterRequest;
import com.koco.guardian.response.ApplicationResponse;
import com.koco.guardian.response.UserAuthResponse;
import com.koco.guardian.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/guardian")
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<ApplicationResponse<UserAuthResponse>> registerUser(
            @RequestBody UserRegisterRequest userRegisterRequest) throws
            KocopeException {
        log.info("Received request to register user with email: {}", userRegisterRequest.getEmail());
        return ResponseEntity.ok(
                ApplicationResponse.getSuccessResponse(userAuthService.registerUser(userRegisterRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApplicationResponse<UserAuthResponse>> loginUser(
            @RequestBody UserLoginRequest userLoginRequest) {
        log.info("Received request to login user with email: {}", userLoginRequest.getEmail());
        return ResponseEntity.ok(ApplicationResponse.getSuccessResponse(userAuthService.loginUser(userLoginRequest)));
    }
}
