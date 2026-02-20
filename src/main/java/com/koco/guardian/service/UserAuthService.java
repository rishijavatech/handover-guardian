package com.koco.guardian.service;

import com.koco.guardian.dto.LoggedInUser;
import com.koco.guardian.entity.UserAuth;
import com.koco.guardian.entity.UserRole;
import com.koco.guardian.entity.UserType;
import com.koco.guardian.enums.ApplicationCode;
import com.koco.guardian.exception.KocopeException;
import com.koco.guardian.repository.UserAuthRepository;
import com.koco.guardian.repository.UserRoleRepository;
import com.koco.guardian.repository.UserTypeRepository;
import com.koco.guardian.request.UserLoginRequest;
import com.koco.guardian.request.UserRegisterRequest;
import com.koco.guardian.request.ResetPasswordRequest;
import com.koco.guardian.request.UserStatusRequest;
import com.koco.guardian.response.UserAuthResponse;
import com.koco.guardian.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserAuthRepository userAuthRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRoleRepository userRoleRepository;
    private final UserTypeRepository userTypeRepository;

    public UserAuthResponse registerUser(UserRegisterRequest userRegisterRequest, LoggedInUser loggedInUser) throws KocopeException {
        String email = userRegisterRequest.getEmail().trim().toLowerCase();
        UserAuth existingUser = userAuthRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            log.error("User already exists with email: {}", email);
            throw new KocopeException(HttpStatus.UNPROCESSABLE_ENTITY,
                    ApplicationCode.USER_ALREADY_REGISTERED_IN_SYSTEM);
        }
        UserRole userRole = userRoleRepository.findByName(userRegisterRequest.getUserRole()).orElseThrow(
                () -> new KocopeException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "No user role exists with role: " + userRegisterRequest.getUserRole()));
        UserType userType = userTypeRepository.findByName(userRegisterRequest.getUserType()).orElseThrow(
                () -> new KocopeException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "No user type exists with type: " + userRegisterRequest.getUserType()));

        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(email);
        userAuth.setRole(userRole);
        userAuth.setType(userType);
        userAuth.setCreatedBy(loggedInUser.getUserId());
        userAuth.setUpdatedBy(loggedInUser.getUserId());
        userAuth.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));

        userAuth.setUserId(userRegisterRequest.getUserId());
        userAuthRepository.save(userAuth);
        log.info("Successfully registered user in auth service with userName: {}", email);
        UserAuthResponse userAuthResponse = new UserAuthResponse();
        userAuthResponse.setEmail(userRegisterRequest.getEmail().trim().toLowerCase());
        userAuthResponse.setUserId(userAuth.getUserId());
        userAuthResponse.setMessage("User registered successfully in auth service");
        return userAuthResponse;
    }

    public UserAuthResponse loginUser(UserLoginRequest userLoginRequest) throws KocopeException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail().trim().toLowerCase(),
                        userLoginRequest.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserAuth userAuth = userAuthRepository.findByEmail(userLoginRequest.getEmail().trim().toLowerCase()).orElse(null);
        if (userAuth == null) {
            log.error("User doesn't exist in system: {}", userLoginRequest.getEmail());
            throw new KocopeException(HttpStatus.UNPROCESSABLE_ENTITY, ApplicationCode.USER_NOT_FOUND_IN_SYSTEM);
        }
        String token = jwtUtil.generateToken(userDetails);
        log.info("Token generated successfully for user : {}", userLoginRequest.getEmail());
        UserAuthResponse userAuthResponse = new UserAuthResponse();
        userAuthResponse.setAuthToken(token);
        userAuthResponse.setEmail(userLoginRequest.getEmail().trim().toLowerCase());
        userAuthResponse.setUserId(userAuth.getUserId());
        userAuthResponse.setMessage("Token generated successfully");
        return userAuthResponse;
    }

    public UserAuthResponse deleteUser(String userId) throws KocopeException {
        UserAuth userAuth = userAuthRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("User not found with userId: {}", userId);
            return new KocopeException(HttpStatus.UNPROCESSABLE_ENTITY, ApplicationCode.USER_NOT_FOUND_IN_SYSTEM);
        });
        boolean existingStatus = userAuth.isDeleted();
        if (existingStatus) {
            throw new KocopeException(HttpStatus.UNPROCESSABLE_ENTITY, ApplicationCode.USER_ALREADY_DELETED);
        }
        userAuth.setDeleted(true);
        userAuthRepository.save(userAuth);
        UserAuthResponse userAuthResponse = new UserAuthResponse();
        userAuthResponse.setUserId(userId);
        userAuthResponse.setMessage("User deleted successfully");
        return userAuthResponse;
    }

    public UserAuthResponse changeStatus(UserStatusRequest userStatusRequest) {
        UserAuth userAuth = userAuthRepository.findByUserId(userStatusRequest.getUserId()).orElse(null);
        if(userAuth != null) {
            userAuth.setActive(userStatusRequest.isActive());
            userAuthRepository.save(userAuth);
            UserAuthResponse userAuthResponse = new UserAuthResponse();
            userAuthResponse.setUserId(userStatusRequest.getUserId());
            userAuthResponse.setMessage("Status changed successfully");
            return userAuthResponse;
        }
        return null;
    }

    public UserAuthResponse resetPassword(ResetPasswordRequest resetPasswordRequest) throws KocopeException {
        String email = resetPasswordRequest.getEmail().trim().toLowerCase();
        String username = resetPasswordRequest.getUsername().trim();
        UserAuth userAuth = userAuthRepository
                .findByUserIdAndEmailAndIsDeletedFalse(username, email)
                .orElseThrow(() -> new KocopeException(HttpStatus.UNPROCESSABLE_ENTITY,
                        ApplicationCode.USER_NOT_FOUND_IN_SYSTEM));

        if (!passwordEncoder.matches(resetPasswordRequest.getOldPassword(), userAuth.getPassword())) {
            throw new KocopeException(HttpStatus.UNAUTHORIZED, "Invalid username/email or old password");
        }

        userAuth.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userAuthRepository.save(userAuth);

        UserAuthResponse userAuthResponse = new UserAuthResponse();
        userAuthResponse.setUserId(userAuth.getUserId());
        userAuthResponse.setEmail(userAuth.getEmail());
        userAuthResponse.setMessage("The password has been reset successfully!");
        return userAuthResponse;
    }
}
