package com.driving.school.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MailBoxController {
    @GetMapping("/mailBox")
    public ModelAndView displayMails() {
        return new ModelAndView("mailBox");
    }
}
