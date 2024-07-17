package com.driving.school.controller;

import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.Test;
import com.driving.school.repository.UserLikedQuestionRepository;
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

import java.util.Collections;
import java.util.List;

@Controller
public class TestController {
    private final TestService testService;
    private final QuestionService questionService;
    private final StudentAnswersTestService studentAnswersTestService;
    private final SchoolUserService schoolUserService;
    private final UserLikedQuestionRepository userLikedQuestionRepository;

    @Autowired
    public TestController(TestService testService, QuestionService questionService, StudentAnswersTestService studentAnswersTestService, SchoolUserService schoolUserService, UserLikedQuestionRepository likedQuestionRepository, UserLikedQuestionRepository userLikedQuestionRepository) {
        this.testService = testService;
        this.questionService = questionService;
        this.studentAnswersTestService = studentAnswersTestService;
        this.schoolUserService = schoolUserService;
        this.userLikedQuestionRepository = userLikedQuestionRepository;
    }

    @GetMapping(value = {"/tests"})
    public ModelAndView displayTestsPage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("tests");
        List<Test> tests = testService.getAllTestsByCategory("B");
        studentAnswersTestService.setStatisticForTest(tests, ((SchoolUser) session.getAttribute("loggedInUser")).getId());
        modelAndView.addObject("tests", tests);
        return modelAndView;
    }

    @PostMapping(value = {"/tests/selectQuestions"})
    public ModelAndView selectQuestions(@RequestParam("testId") Long testId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("selectedQuestionsInTest");
        Long userId = ((SchoolUser) session.getAttribute("loggedInUser")).getId();
        List<Test> tests = Collections.singletonList(testService.getTestById(testId));
        studentAnswersTestService.setStatisticForTest(tests, ((SchoolUser) session.getAttribute("loggedInUser")).getId());
        modelAndView.addObject("test", tests.getFirst());
        Integer numberCorrectAnswers = studentAnswersTestService.getCorrectStudentAnswersTestByUserIdandTestId(userId, testId).size();
        Integer numberIncorrectAnswers = studentAnswersTestService.getInCorrectStudentAnswersTestByUserIdandTestId(userId, testId).size();
        Integer numberSkippedAnswers = studentAnswersTestService.getSkippedStudentAnswersTestByUserIdandTestId(userId, testId).size();
        Integer numberLikedQuestions = schoolUserService.findAllLikedQuestionsByUserIdAndTestId(userId, testId).size();
        Integer numberRemainingAnswers = tests.getFirst().getNumberQuestions() - numberCorrectAnswers - numberIncorrectAnswers - numberSkippedAnswers;
        modelAndView.addObject("numberCorrectAnswers", numberCorrectAnswers);
        modelAndView.addObject("numberIncorrectAnswers", numberIncorrectAnswers);
        modelAndView.addObject("numberSkippedAnswers", numberSkippedAnswers);
        modelAndView.addObject("numberLikedQuestions", numberLikedQuestions);
        modelAndView.addObject("numberRemainingAnswers", numberRemainingAnswers);
        return modelAndView;
    }

    @PostMapping(value = {"/tests/solveTest"})
    public ModelAndView getTestToSolve(@RequestParam("testId") Long testId, @RequestParam("selectedTypeQuestions") String selectedTypeQuestions, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("solveTest");
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Question question = questionService.getNextQuestion(testId, user, selectedTypeQuestions);
        modelAndView.addObject("question", question);
        modelAndView.addObject("test", testService.getTestById(testId));
        modelAndView.addObject("selectedTypeQuestions", selectedTypeQuestions);
        return modelAndView;
    }

    @PostMapping(value = {"/tests/action"})
    public ModelAndView getActionFromTest(@RequestParam("testId") Long testId, @RequestParam("questionId") Long questionId, @RequestParam("action") String action, @RequestParam(value = "isLiked", required = false, defaultValue = "false") Boolean isLiked, HttpSession session) {
        ModelAndView modelAndView;
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        switch (action) {
            case "A":
            case "B":
            case "C":
            case "TAK":
            case "NIE":
                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session);
                modelAndView.setViewName("answerResultTest");
                modelAndView.addObject("answer", studentAnswersTestService.save(user, testId, questionId, action, isLiked));
                break;

            case "SKIP":
                studentAnswersTestService.save(user, testId, questionId, action, isLiked);
                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session);
                break;

            case "BACK":
                modelAndView = displayTestsPage(session);
                break;

            default:
                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session);
                break;
        }

        if (isLiked)
            schoolUserService.addLikedQuestionToUser(questionId, testId, user);
        else
            schoolUserService.deleteLikedQuestionFromUser(questionId, testId, user);

        return modelAndView;
    }
}
