package com.driving.school.controller;

import com.driving.school.model.User;
import com.driving.school.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"", "/", "/home"})
    public ModelAndView displayHomePage() {
        ModelAndView m = new ModelAndView("home");
        m.addObject("registerUser", new User());
        m.addObject("loginUser", new User());
        return m;
    }

    @PostMapping(value = "/registerUser")
    public ModelAndView createUser(@ModelAttribute("registerUser") User user) {
        ModelAndView m = new ModelAndView("home");
        boolean isSaved = userService.createNewUser(user);
        if (isSaved)
            m.setViewName("redirect:/login?register=true#login");
        else
            m.setViewName("redirect:/login?register=false#login");

        return m;
    }
}
