package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.Test;
import com.driving.school.service.QuestionService;
import com.driving.school.service.SchoolUserService;
import com.driving.school.service.StudentAnswersTestService;
import com.driving.school.service.TestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class TestController {
    private final TestService testService;
    private final QuestionService questionService;
    private final StudentAnswersTestService studentAnswersTestService;
    private final SchoolUserService schoolUserService;

    @Autowired
    public TestController(TestService testService, QuestionService questionService, StudentAnswersTestService studentAnswersTestService, SchoolUserService schoolUserService) {
        this.testService = testService;
        this.questionService = questionService;
        this.studentAnswersTestService = studentAnswersTestService;
        this.schoolUserService = schoolUserService;
    }

    @GetMapping(value = {"/tests"})
    public ModelAndView displayTestsPage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("tests");
        List<Test> tests = testService.getAllTestsByCategory("B");
        studentAnswersTestService.setStatisticForTest(tests, ((SchoolUser) session.getAttribute("loggedInUser")).getId());
        modelAndView.addObject("tests", tests);
        return modelAndView;
    }

    @PostMapping(value = {"/tests/solveTest"})
    public ModelAndView getTestToSolve(@RequestParam("testId") Long testId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("solveTest");
        modelAndView.addObject("question", questionService.getNextQuestion(testId, ((SchoolUser) session.getAttribute("loggedInUser")).getId()));
        modelAndView.addObject("test", testService.getTestById(testId));
        return modelAndView;
    }

    @PostMapping(value = {"/tests/action"})
    public ModelAndView getActionFromTest(@RequestParam("testId") Long testId, @RequestParam("questionId") Long questionId, @RequestParam("action") String action, @RequestParam(value = "isLiked", required = false, defaultValue = "false") Boolean isLiked, HttpSession session) {
        ModelAndView modelAndView;

        if (isLiked)
            schoolUserService.addLikedQuestionToUser(questionId, ((SchoolUser) session.getAttribute("loggedInUser")).getId());

        switch (action) {
            case "A":
            case "B":
            case "C":
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
