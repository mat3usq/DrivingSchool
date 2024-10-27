package com.driving.school.components;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggedInUserInterceptor implements HandlerInterceptor {
    private final SchoolUserRepository schoolUserRepository;

    @Autowired
    public LoggedInUserInterceptor(SchoolUserRepository schoolUserRepository) {
        this.schoolUserRepository = schoolUserRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (request.getSession().getAttribute("loggedInUser") == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                SchoolUser user = schoolUserRepository.findByEmail(auth.getName());
                request.getSession().setAttribute("loggedInUser", user);
            }
        }
        return true;
    }

}
