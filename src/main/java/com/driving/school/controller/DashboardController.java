package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.SchoolUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {
    private final SchoolUserService schoolUserService;

    @Autowired
    public DashboardController(SchoolUserService schoolUserService) {
        this.schoolUserService = schoolUserService;
    }

    @GetMapping("/dashboard")
    public ModelAndView displayDashboard(Authentication authentication, HttpSession session) {
        SchoolUser user = schoolUserService.findUserByEmail(authentication.getName());
        if (user != null)
            session.setAttribute("loggedInUser", user);
        return new ModelAndView("dashboard");
    }

    @PostMapping("/dashboard/changeCurrentCategory")
    public ModelAndView changeCategory(HttpSession session, @RequestParam("categoryId") Long categoryId) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        schoolUserService.changeCategory(user, categoryId);
        return new ModelAndView("dashboard");
    }
}
