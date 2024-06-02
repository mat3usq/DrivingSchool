package com.driving.school.controller;

import com.driving.school.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {
    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping(value = {"/tests"})
    public ModelAndView displayTestsPage() {
        ModelAndView modelAndView = new ModelAndView("tests");
        modelAndView.addObject("tests", testService.getAllTestsByCategory("B"));
        return modelAndView;
    }
}
