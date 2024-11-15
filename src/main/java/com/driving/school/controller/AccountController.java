package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {
    private final SchoolUserRepository schoolUserRepository;

    @Autowired
    public AccountController(SchoolUserRepository schoolUserRepository) {
        this.schoolUserRepository = schoolUserRepository;
    }

    @GetMapping("/account")
    public ModelAndView displayAccount(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("account");
        SchoolUser user = schoolUserRepository.findByEmail(((SchoolUser) session.getAttribute("loggedInUser")).getEmail());
        if (user != null) {
            modelAndView.addObject("user", user);
            return modelAndView;
        }
        return new ModelAndView("redirect:/dashboard");
    }

    @GetMapping("/notifications")
    public ModelAndView displayNotifications(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("notifications");
        SchoolUser user = schoolUserRepository.findByEmail(((SchoolUser) session.getAttribute("loggedInUser")).getEmail());
        if (user != null) {
            modelAndView.addObject("user", user);
            return modelAndView;
        }
        return new ModelAndView("redirect:/dashboard");
    }
}
