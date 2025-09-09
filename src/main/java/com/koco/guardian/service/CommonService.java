package com.koco.guardian.service;

import com.koco.guardian.dto.LoggedInUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CommonService {

    public LoggedInUser getLoggedInUserFromRequest(HttpServletRequest httpServletRequest) {
        httpServletRequest.getHeaderNames();
        String userId = httpServletRequest.getHeader("X-User-Id");
        String role = httpServletRequest.getHeader("X-User-Role");

        LoggedInUser user = new LoggedInUser();
        user.setUserId(userId);

        return user;
    }


    public LoggedInUser getLoggedInUserFromRequest() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        if(httpServletRequest != null) {
            String userId = httpServletRequest.getHeader("X-User-Id");
            String role = httpServletRequest.getHeader("X-User-Role");
            LoggedInUser user = new LoggedInUser();
            user.setUserId(userId);
            return user;
        }
        return null;
    }

    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        if (attribs instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) attribs).getRequest();
        }
        return null;
    }
}
