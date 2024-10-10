package com.driving.school.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {
    @GetMapping("/account")
    public ModelAndView displayAccount() {
        return new ModelAndView("account");
    }
}
