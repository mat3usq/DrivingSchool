package com.driving.school.security;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.SchoolUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final SchoolUserRepository schoolUserRepository;

    @Autowired
    public CustomAuthenticationSuccessHandler(SchoolUserRepository schoolUserRepository) {
        this.schoolUserRepository = schoolUserRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        SchoolUser user = schoolUserRepository.findByEmail(authentication.getName());
        request.getSession().setAttribute("loggedInUser", user);
        response.sendRedirect("/dashboard");
    }
}
