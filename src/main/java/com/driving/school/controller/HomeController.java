package com.driving.school.controller;

import com.driving.school.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    @GetMapping(value = {"/home"})
    public ModelAndView displayHomePage() {
        ModelAndView m = new ModelAndView("home");
        m.addObject("registerUser", new User());
        return m;
    }
}
