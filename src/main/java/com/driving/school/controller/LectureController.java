package com.driving.school.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LectureController {
    @GetMapping(value = "/lecture")
    public ModelAndView displayLecturePage() {
        ModelAndView m = new ModelAndView("lecture");
        return m;
    }
}
