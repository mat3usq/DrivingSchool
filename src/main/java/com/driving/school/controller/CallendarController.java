package com.driving.school.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CallendarController {
    @GetMapping("/calendar")
    public ModelAndView displayCalendar() {
        return new ModelAndView("calendar");
    }
}
