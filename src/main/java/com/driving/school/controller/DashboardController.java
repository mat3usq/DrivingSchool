package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {
    private final SchoolUserRepository schoolUserRepository;

    @Autowired
    public DashboardController(SchoolUserRepository schoolUserRepository) {
        this.schoolUserRepository = schoolUserRepository;
    }

    @GetMapping("/dashboard")
    public ModelAndView displayDashboard(Authentication authentication, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("dashboard");
        SchoolUser user = schoolUserRepository.findByEmail(authentication.getName());
        if (user != null)
            session.setAttribute("loggedInUser", user);
        return modelAndView;
    }
}
