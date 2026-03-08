package com.koco.guardian.service;

import com.koco.guardian.dto.LoggedInUser;
import com.koco.guardian.entity.UserAuth;
import com.koco.guardian.entity.UserRole;
import com.koco.guardian.entity.UserType;
import com.koco.guardian.exception.KocopeException;
import com.koco.guardian.repository.UserAuthRepository;
import com.koco.guardian.repository.UserRoleRepository;
import com.koco.guardian.repository.UserTypeRepository;
import com.koco.guardian.request.UserLoginRequest;
import com.koco.guardian.request.UserRegisterRequest;
import com.koco.guardian.request.UserStatusRequest;
import com.koco.guardian.response.UserAuthResponse;
import com.koco.guardian.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserAuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserAuthRepository userAuthRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_success() throws KocopeException {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setEmail("test@koco.com");
        req.setUserRole("Admin");
        req.setUserType("Normal");
        req.setPassword("password");
        req.setUserId("U001");
        LoggedInUser loggedUser = new LoggedInUser();
        loggedUser.setUserId("creator1");

        when(userAuthRepository.findByEmail("test@koco.com")).thenReturn(Optional.empty());
        UserRole userRole = new UserRole(); userRole.setName("Admin");
        UserType userType = new UserType(); userType.setName("Normal");
        when(userRoleRepository.findByName("Admin")).thenReturn(Optional.of(userRole));
        when(userTypeRepository.findByName("Normal")).thenReturn(Optional.of(userType));
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userAuthRepository.save(any(UserAuth.class))).thenAnswer(i -> i.getArguments()[0]);

        UserAuthResponse resp = userAuthService.registerUser(req, loggedUser);
        assertEquals("test@koco.com", resp.getEmail());
        assertEquals("U001", resp.getUserId());
        assertEquals("User registered successfully in auth service", resp.getMessage());
    }

    @Test
    void registerUser_alreadyExists_throwsException() {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setEmail("exist@koco.com");

        when(userAuthRepository.findByEmail("exist@koco.com")).thenReturn(Optional.of(new UserAuth()));

        assertThrows(KocopeException.class, () ->
                userAuthService.registerUser(req, new LoggedInUser())
        );
    }

    @Test
    void loginUser_success() throws KocopeException {
        UserLoginRequest req = new UserLoginRequest();
        req.setEmail("test@koco.com");
        req.setPassword("password");
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("tokentest");
        UserAuthResponse resp = userAuthService.loginUser(req);
        assertEquals("test@koco.com", resp.getEmail());
        assertEquals("Token generated successfully", resp.getMessage());
        assertEquals("tokentest", resp.getAuthToken());
    }

    @Test
    void deleteUser_success() throws KocopeException {
        UserAuth user = new UserAuth();
        user.setUserId("u002");
        user.setDeleted(false);
        when(userAuthRepository.findByUserId("u002")).thenReturn(Optional.of(user));
        when(userAuthRepository.save(any(UserAuth.class))).thenReturn(user);

        UserAuthResponse resp = userAuthService.deleteUser("u002");
        assertEquals("u002", resp.getUserId());
        assertEquals("User deleted successfully", resp.getMessage());
        assertTrue(user.isDeleted());
    }

    @Test
    void deleteUser_notFound_throwsException() {
        when(userAuthRepository.findByUserId("xyz")).thenReturn(Optional.empty());
        assertThrows(KocopeException.class, () -> userAuthService.deleteUser("xyz"));
    }

    @Test
    void deleteUser_alreadyDeleted_throwsException() {
        UserAuth user = new UserAuth();
        user.setUserId("u002");
        user.setDeleted(true);
        when(userAuthRepository.findByUserId("u002")).thenReturn(Optional.of(user));
        assertThrows(KocopeException.class, () -> userAuthService.deleteUser("u002"));
    }

    @Test
    void changeStatus_success() {
        UserStatusRequest req = new UserStatusRequest();
        req.setUserId("u002");
        req.setActive(true);

        UserAuth user = new UserAuth();
        user.setUserId("u002");
        user.setActive(false);

        when(userAuthRepository.findByUserId("u002")).thenReturn(Optional.of(user));
        when(userAuthRepository.save(any(UserAuth.class))).thenReturn(user);

        UserAuthResponse resp = userAuthService.changeStatus(req);
        assertEquals("u002", resp.getUserId());
        assertEquals("Status changed successfully", resp.getMessage());
        assertTrue(user.isActive());
    }

    @Test
    void changeStatus_userNotFound_returnsNull() {
        UserStatusRequest req = new UserStatusRequest();
        req.setUserId("none");
        req.setActive(true);

        when(userAuthRepository.findByUserId("none")).thenReturn(Optional.empty());
        UserAuthResponse resp = userAuthService.changeStatus(req);
        assertNull(resp);
    }
    @Test
    void registerUser_missingUserRole_throwsException() {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setEmail("test@koco.com");
        req.setUserRole("InvalidRole");
        req.setUserType("Normal");
        req.setPassword("password");
        LoggedInUser loggedUser = new LoggedInUser();
        loggedUser.setUserId("creator1");
        when(userAuthRepository.findByEmail("test@koco.com")).thenReturn(Optional.empty());
        when(userRoleRepository.findByName("InvalidRole")).thenReturn(Optional.empty());
        KocopeException ex = assertThrows(KocopeException.class, () -> userAuthService.registerUser(req, loggedUser));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("No user role exists"));
    }
    @Test
    void registerUser_missingUserType_throwsException() {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setEmail("test@koco.com");
        req.setUserRole("Admin");
        req.setUserType("InvalidType");
        req.setPassword("password");
        LoggedInUser loggedUser = new LoggedInUser();
        loggedUser.setUserId("creator1");
        when(userAuthRepository.findByEmail("test@koco.com")).thenReturn(Optional.empty());
        when(userRoleRepository.findByName("Admin")).thenReturn(Optional.of(new UserRole()));
        when(userTypeRepository.findByName("InvalidType")).thenReturn(Optional.empty());
        KocopeException ex = assertThrows(KocopeException.class, () -> userAuthService.registerUser(req, loggedUser));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("No user type exists"));
    }
}
