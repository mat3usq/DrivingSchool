package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.repository.StudentAnswersTestRepository;
import com.driving.school.service.QuestionService;
import com.driving.school.service.StudentAnswersTestService;
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
    private final StudentAnswersTestService studentAnswersTestService;

    @Autowired
    public TestController(TestService testService, QuestionService questionService, StudentAnswersTestService studentAnswersTestService) {
        this.testService = testService;
        this.questionService = questionService;
        this.studentAnswersTestService = studentAnswersTestService;
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
        modelAndView.addObject("test", testService.getTestById(testId));
        return modelAndView;
    }

    @PostMapping(value = {"/tests/action"})
    public ModelAndView getActionFromTest(@RequestParam("testId") Long testId, @RequestParam("questionId") Long questionId, @RequestParam("action") String action, HttpSession session) {
        ModelAndView modelAndView;

        switch (action) {
            case "TAK":
            case "NIE":
                modelAndView = getTestToSolve(testId, session);
                modelAndView.setViewName("answerResultTest");
                modelAndView.addObject("answer", studentAnswersTestService.save((SchoolUser) session.getAttribute("loggedInUser"), testId, questionId, action));
                break;

            case "SKIP":
                studentAnswersTestService.save((SchoolUser) session.getAttribute("loggedInUser"), testId, questionId, action);
                modelAndView = getTestToSolve(testId, session);
                break;

            default:
                modelAndView = getTestToSolve(testId, session);
                break;
        }

        return modelAndView;
    }
}
