package com.driving.school.components;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

@Component
public class LoggedInUserInterceptor implements HandlerInterceptor {
    private final SchoolUserRepository schoolUserRepository;

    @Autowired
    public LoggedInUserInterceptor(SchoolUserRepository schoolUserRepository) {
        this.schoolUserRepository = schoolUserRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            SchoolUser currentUser = schoolUserRepository.findByEmail(auth.getName());
            SchoolUser sessionUser = (SchoolUser) request.getSession().getAttribute("loggedInUser");

            if (sessionUser != null &&  !currentUser.getRoleName().equals(sessionUser.getRoleName()))
                updateSecurityContext(currentUser);

            request.getSession().setAttribute("loggedInUser", currentUser);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            SchoolUser currentUser = schoolUserRepository.findByEmail(auth.getName());
            SchoolUser sessionUser = (SchoolUser) request.getSession().getAttribute("loggedInUser");

            if (sessionUser != null && !currentUser.getRoleName().equals(sessionUser.getRoleName())) {
                updateSecurityContext(currentUser);
                request.getSession().setAttribute("loggedInUser", currentUser);
            }
        }
    }

    private void updateSecurityContext(SchoolUser user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return;

        GrantedAuthority newAuthority = new SimpleGrantedAuthority("ROLE_" + user.getRoleName());

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                Collections.singleton(newAuthority)
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
