package com.koco.guardian.controller;

import com.koco.guardian.dto.LoggedInUser;
import com.koco.guardian.exception.KocopeException;
import com.koco.guardian.request.UserLoginRequest;
import com.koco.guardian.request.UserRegisterRequest;
import com.koco.guardian.request.UserStatusRequest;
import com.koco.guardian.response.ApplicationResponse;
import com.koco.guardian.response.UserAuthResponse;
import com.koco.guardian.service.CommonService;
import com.koco.guardian.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final CommonService commonService;

    @PostMapping("/register")
    public ResponseEntity<ApplicationResponse<UserAuthResponse>> registerUser(HttpServletRequest httpServletRequest,
            @RequestBody @Valid UserRegisterRequest userRegisterRequest) throws
            KocopeException {
        log.info("Received request to register user with email: {}", userRegisterRequest.getEmail());
        LoggedInUser loggedInUser = commonService.getLoggedInUserFromRequest(httpServletRequest);
        return ResponseEntity.ok(
                ApplicationResponse.getSuccessResponse(
                        userAuthService.registerUser(userRegisterRequest, loggedInUser)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApplicationResponse<UserAuthResponse>> loginUser(
            @RequestBody @Valid UserLoginRequest userLoginRequest) throws KocopeException {
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

    @PostMapping("/change-status")
    public ResponseEntity<ApplicationResponse<UserAuthResponse>> changeStatus(HttpServletRequest httpServletRequest,
            @RequestBody @Valid UserStatusRequest userStatusRequest) {
        log.info("Received request to change status for user: {}", userStatusRequest.getUserId());
        LoggedInUser loggedInUser = commonService.getLoggedInUserFromRequest(httpServletRequest);
        return ResponseEntity.ok(
                ApplicationResponse.getSuccessResponse(userAuthService.changeStatus(userStatusRequest)));

    }

}
