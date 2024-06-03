package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.service.QuestionService;
import com.driving.school.service.TestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {
    private final TestService testService;
    private final QuestionService questionService;

    @Autowired
    public TestController(TestService testService, QuestionService questionService) {
        this.testService = testService;
        this.questionService = questionService;
    }

    @GetMapping(value = {"/tests"})
    public ModelAndView displayTestsPage() {
        ModelAndView modelAndView = new ModelAndView("tests");
        modelAndView.addObject("tests", testService.getAllTestsByCategory("B"));
        return modelAndView;
    }

    @PostMapping(value = {"/tests/solveTest"})
    public ModelAndView getTestToSolve(@RequestParam("testId") Long testId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("solveBasicTest");
        modelAndView.addObject("question", questionService.getNextQuestion(testId, ((SchoolUser) session.getAttribute("loggedInUser")).getId()));
        return modelAndView;
    }
}
