package com.driving.school.controller;

import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.model.Test;
import com.driving.school.repository.StudentAnswersTestRepository;
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

import java.time.LocalDateTime;
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
    public TestController(TestService testService, QuestionService questionService, StudentAnswersTestService studentAnswersTestService, SchoolUserService schoolUserService, UserLikedQuestionRepository userLikedQuestionRepository, StudentAnswersTestRepository studentAnswersTestRepository) {
        this.testService = testService;
        this.questionService = questionService;
        this.studentAnswersTestService = studentAnswersTestService;
        this.schoolUserService = schoolUserService;
        this.userLikedQuestionRepository = userLikedQuestionRepository;
    }

    @GetMapping(value = {"/tests"})
    public ModelAndView displayTestsPage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("tests");
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (user.getCurrentCategory().isEmpty())
            return new ModelAndView("redirect:/dashboard");

        List<Test> tests = testService.getAllTestsByCategory(user.getCurrentCategory());
        studentAnswersTestService.setStatisticForTest(tests, user.getId());
        modelAndView.addObject("tests", tests);
        return modelAndView;
    }

    @PostMapping(value = {"/tests/selectQuestions"})
    public ModelAndView selectQuestions(@RequestParam("testId") Long testId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("selectedQuestionsInTest");
        SchoolUser schoolUser = (SchoolUser) session.getAttribute("loggedInUser");
        Long userId = schoolUser.getId();
        List<Test> tests = Collections.singletonList(testService.getTestById(testId));

        if (tests.getFirst() == null || !tests.getFirst().getDrivingCategory().equals(schoolUser.getCurrentCategory()))
            return displayTestsPage(session);

        studentAnswersTestService.setStatisticForTest(tests, ((SchoolUser) session.getAttribute("loggedInUser")).getId());
        modelAndView.addObject("test", tests.getFirst());
        Integer numberCorrectAnswers = studentAnswersTestService.getCorrectStudentAnswersTestByUserIdandTestId(userId, testId).size();
        Integer numberIncorrectAnswers = studentAnswersTestService.getInCorrectStudentAnswersTestByUserIdandTestId(userId, testId).size();
        Integer numberSkippedAnswers = studentAnswersTestService.getSkippedStudentAnswersTestByUserIdandTestId(userId, testId).size();
        int numberLikedQuestions = schoolUserService.findAllLikedQuestionsByUserIdAndTestId(userId, testId).size();
        int numberRemainingAnswers = tests.getFirst().getNumberQuestions() - numberCorrectAnswers - numberIncorrectAnswers - numberSkippedAnswers;
        int percentagesCorrectAnswers = 0;
        int percentagesIncorrectAnswers = 0;
        int percentagesSkippedAnswers = 0;
        int percentagesLikedQuestions = 0;
        int percentagesRemainingAnswers = 0;

        if (tests.getFirst().getNumberQuestions() != 0) {
            percentagesCorrectAnswers = numberCorrectAnswers * 100 / tests.getFirst().getNumberQuestions();
            percentagesIncorrectAnswers = numberIncorrectAnswers * 100 / tests.getFirst().getNumberQuestions();
            percentagesSkippedAnswers = numberSkippedAnswers * 100 / tests.getFirst().getNumberQuestions();
            percentagesLikedQuestions = numberLikedQuestions * 100 / tests.getFirst().getNumberQuestions();
            percentagesRemainingAnswers = numberRemainingAnswers * 100 / tests.getFirst().getNumberQuestions();
        }

        modelAndView.addObject("numberCorrectAnswers", numberCorrectAnswers);
        modelAndView.addObject("percentagesCorrectAnswers", percentagesCorrectAnswers);

        modelAndView.addObject("numberIncorrectAnswers", numberIncorrectAnswers);
        modelAndView.addObject("percentagesIncorrectAnswers", percentagesIncorrectAnswers);

        modelAndView.addObject("numberSkippedAnswers", numberSkippedAnswers);
        modelAndView.addObject("percentagesSkippedAnswers", percentagesSkippedAnswers);

        modelAndView.addObject("numberLikedQuestions", numberLikedQuestions);
        modelAndView.addObject("percentagesLikedQuestions", percentagesLikedQuestions);

        modelAndView.addObject("numberRemainingAnswers", numberRemainingAnswers);
        modelAndView.addObject("percentagesRemainingAnswers", percentagesRemainingAnswers);

        modelAndView.addObject("allDone", tests.getFirst().getNumberQuestions() != 0 && numberCorrectAnswers + numberIncorrectAnswers + numberSkippedAnswers == tests.getFirst().getNumberQuestions());
        return modelAndView;
    }

    @PostMapping(value = {"/tests/solveTest"})
    public ModelAndView getTestToSolve(@RequestParam("testId") Long testId, @RequestParam("selectedTypeQuestions") String selectedTypeQuestions, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("solveTest");
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Question question = questionService.getNextQuestion(testId, user, selectedTypeQuestions);
        Test test = testService.getTestById(testId);

        if (test == null || !test.getDrivingCategory().equals(user.getCurrentCategory()))
            return displayTestsPage(session);

        modelAndView.addObject("question", question);
        modelAndView.addObject("test", test);
        modelAndView.addObject("selectedTypeQuestions", selectedTypeQuestions);
        modelAndView.addObject("isLiked", question.getId() != null && userLikedQuestionRepository.findBySchoolUserAndQuestionIdAndTestId(user, question.getId(), testId) != null);
        session.setAttribute("timeStartAnswer", LocalDateTime.now());
        return modelAndView;
    }

    @PostMapping(value = {"/tests/action"})
    public ModelAndView getActionFromTest(@RequestParam("testId") Long testId, @RequestParam(value = "questionId") Long questionId, @RequestParam("action") String action, @RequestParam(value = "isLiked", required = false, defaultValue = "false") Boolean isLiked, HttpSession session) {
        ModelAndView modelAndView;
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Test test = testService.getTestById(testId);
        LocalDateTime timeStartAnswer = (LocalDateTime) session.getAttribute("timeStartAnswer");

        if (test == null || !test.getDrivingCategory().equals(user.getCurrentCategory()))
            return displayTestsPage(session);

        switch (action) {
            case "A":
            case "B":
            case "C":
            case "TAK":
            case "NIE":
                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session);
                modelAndView.setViewName("answerResultTest");
                modelAndView.addObject("answer", studentAnswersTestService.save(user, testId, questionId, action, isLiked, timeStartAnswer));
                modelAndView.addObject("isLiked", isLiked);
                break;

            case "SKIP":
                studentAnswersTestService.save(user, testId, questionId, action, isLiked, timeStartAnswer);
                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session);
                break;

            default:
                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session);
                break;
        }

        if (isLiked)
            schoolUserService.addLikedQuestionToUser(questionId, testId, user);
        else
            schoolUserService.deleteLikedQuestionFromUser(questionId, testId, user);

        if (action.equals("BACK"))
            modelAndView = selectQuestions(testId, session);

        return modelAndView;
    }

    @PostMapping(value = {"/tests/resetTest"})
    public ModelAndView resetTest(@RequestParam("testId") Long testId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Test test = testService.getTestById(testId);
        if (test == null || !test.getDrivingCategory().equals(user.getCurrentCategory()))
            return displayTestsPage(session);
        List<StudentAnswersTest> answers = studentAnswersTestService.findAllBySchoolUserAndTest(user, test);
        if (answers.size() == test.getNumberQuestions())
            studentAnswersTestService.deleteAllStudentAnswersTest(answers);
        return selectQuestions(testId, session);
    }
}
