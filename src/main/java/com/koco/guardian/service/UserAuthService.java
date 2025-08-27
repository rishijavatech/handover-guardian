package com.koco.guardian.service;

import com.koco.guardian.entity.UserAuth;
import com.koco.guardian.enums.ResponseCode;
import com.koco.guardian.exception.KocopeException;
import com.koco.guardian.feignclient.PatronClient;
import com.koco.guardian.repository.UserAuthRepository;
import com.koco.guardian.request.UserLoginRequest;
import com.koco.guardian.request.UserRegisterRequest;
import com.koco.guardian.response.UserAuthResponse;
import com.koco.guardian.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserAuthRepository userAuthRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PatronClient patronClient;

    @Transactional(rollbackFor = Throwable.class)
    public UserAuthResponse registerUser(UserRegisterRequest userRegisterRequest) throws KocopeException {
        String email = userRegisterRequest.getEmail();
        UserAuth existingUser = userAuthRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            log.error("User already exists with email: {}", email);
            throw new KocopeException(HttpStatus.UNPROCESSABLE_ENTITY, ResponseCode.USER_ALREADY_REGISTERED_IN_SYSTEM);
        }
        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(email);
        userAuth.setRole(userRegisterRequest.getUserRole());
        userAuth.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));

        //TODO need to finalise the logic for user id creation
        String userId = RandomStringUtils.randomAlphanumeric(20);
        userAuth.setUserId(userId);
        userAuthRepository.save(userAuth);
        log.info("Successfully registered user in auth service with userName: {}", email);
        userRegisterRequest.setUserId(userId);
        patronClient.createUser(userRegisterRequest);
        UserAuthResponse userAuthResponse = new UserAuthResponse();
        userAuthResponse.setUserName(userRegisterRequest.getEmail());
        userAuthResponse.setMessage("User registered successfully");
        return userAuthResponse;
    }

    public UserAuthResponse loginUser(UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(),
                        userLoginRequest.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        log.info("Token generated successfully for user : {}", userLoginRequest.getEmail());
        return new UserAuthResponse(userLoginRequest.getEmail(), token, "Token " +
                "generated successfully");
    }
}
