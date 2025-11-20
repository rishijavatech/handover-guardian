package com.koco.guardian.service;

import com.koco.guardian.dto.LoggedInUser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommonServiceTest {

    private CommonService commonService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commonService = new CommonService();
    }

    @Test
    void testGetLoggedInUserFromRequest_WithRequest() {
        when(httpServletRequest.getHeader("X-User-Id")).thenReturn("user123");
        when(httpServletRequest.getHeader("X-User-Role")).thenReturn("Admin");

        LoggedInUser user = commonService.getLoggedInUserFromRequest(httpServletRequest);

        assertNotNull(user);
        assertEquals("user123", user.getUserId());
    }

    @Test
    void testGetLoggedInUserFromRequest_NoRequestHeader() {
        when(httpServletRequest.getHeader("X-User-Id")).thenReturn(null);
        when(httpServletRequest.getHeader("X-User-Role")).thenReturn(null);

        LoggedInUser user = commonService.getLoggedInUserFromRequest(httpServletRequest);

        assertNotNull(user);
        assertNull(user.getUserId());
    }

    @Test
    void testGetLoggedInUserFromRequest_NoArgs_WithRequestInContext() {
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        when(httpServletRequest.getHeader("X-User-Id")).thenReturn("user456");
        when(httpServletRequest.getHeader("X-User-Role")).thenReturn("User");

        LoggedInUser user = commonService.getLoggedInUserFromRequest();

        assertNotNull(user);
        assertEquals("user456", user.getUserId());

        // Cleanup the RequestContextHolder
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetLoggedInUserFromRequest_NoArgs_NoRequestInContext() {
        RequestContextHolder.resetRequestAttributes();

        LoggedInUser user = commonService.getLoggedInUserFromRequest();

        assertNull(user);
    }
}
