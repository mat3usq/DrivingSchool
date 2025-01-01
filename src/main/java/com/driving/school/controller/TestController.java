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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public ModelAndView displayTestsPage(HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("tests");
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (user.getCurrentCategory().isEmpty()) {
            redirectAttributes.addFlashAttribute("notChoosenCategoryInfo", "Proszę wybierz kategorię, aby pomyślnie rozwiązać test!");
            return new ModelAndView("redirect:/dashboard");
        }

        List<Test> tests = testService.getAllTestsByCategory(user.getCurrentCategory());
        studentAnswersTestService.setStatisticForTest(tests, user.getId());
        modelAndView.addObject("tests", tests);
        modelAndView.addObject("hasSpecificTests", tests.stream().anyMatch(test -> test.getTestType()));
        modelAndView.addObject("hasNoSpecificTests", tests.stream().anyMatch(test -> !test.getTestType()));
        return modelAndView;
    }

    @PostMapping(value = {"/tests/selectQuestions"})
    public ModelAndView selectQuestions(@RequestParam("testId") Long testId, HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("selectedQuestionsInTest");
        SchoolUser schoolUser = (SchoolUser) session.getAttribute("loggedInUser");
        Long userId = schoolUser.getId();
        List<Test> tests = Collections.singletonList(testService.getTestById(testId));

        if (tests.getFirst() == null || !tests.getFirst().getDrivingCategory().equals(schoolUser.getCurrentCategory()))
            return displayTestsPage(session, redirectAttributes);

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
    public ModelAndView getTestToSolve(@RequestParam("testId") Long testId, @RequestParam("selectedTypeQuestions") String selectedTypeQuestions, HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("solveTest");
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Question question = questionService.getNextQuestion(testId, user, selectedTypeQuestions);
        Test test = testService.getTestById(testId);

        if (test == null || !test.getDrivingCategory().equals(user.getCurrentCategory()))
            return displayTestsPage(session, redirectAttributes);

        modelAndView.addObject("question", question);
        modelAndView.addObject("test", test);
        modelAndView.addObject("selectedTypeQuestions", selectedTypeQuestions);
        modelAndView.addObject("isLiked", question.getId() != null && userLikedQuestionRepository.findBySchoolUserAndQuestionIdAndTestId(user, question.getId(), testId) != null);
        session.setAttribute("timeStartAnswer", LocalDateTime.now());
        return modelAndView;
    }

    @PostMapping(value = {"/tests/action"})
    public ModelAndView getActionFromTest(@RequestParam("testId") Long testId, @RequestParam(value = "questionId") Long questionId, @RequestParam("action") String action, @RequestParam(value = "isLiked", required = false, defaultValue = "false") Boolean isLiked, HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView;
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Test test = testService.getTestById(testId);
        LocalDateTime timeStartAnswer = (LocalDateTime) session.getAttribute("timeStartAnswer");

        if (test == null || !test.getDrivingCategory().equals(user.getCurrentCategory()))
            return displayTestsPage(session, redirectAttributes);

        if (isLiked)
            schoolUserService.addLikedQuestionToUser(questionId, testId, user);
        else if (!user.getSelectedTypeQuestions().equals("likedQuestions"))
            schoolUserService.deleteLikedQuestionFromUser(questionId, testId, user);

        switch (action) {
            case "A":
            case "B":
            case "C":
            case "TAK":
            case "NIE":
                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session, redirectAttributes);
                modelAndView.setViewName("answerResultTest");
                modelAndView.addObject("answer", studentAnswersTestService.save(user, testId, questionId, action, timeStartAnswer));
                modelAndView.addObject("isLiked", isLiked);

                if (user.getSelectedTypeQuestions().equals("likedQuestions") && !isLiked)
                    schoolUserService.deleteLikedQuestionFromUser(questionId, testId, user);
                break;

            case "SKIP":
                if (user.getSelectedTypeQuestions().equals("likedQuestions"))
                    schoolUserService.deleteLikedQuestionFromUser(questionId, testId, user);

                studentAnswersTestService.save(user, testId, questionId, action, timeStartAnswer);
                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session, redirectAttributes);

                if (user.getSelectedTypeQuestions().equals("likedQuestions") && isLiked)
                    schoolUserService.addLikedQuestionToUser(questionId, testId, user);
                break;

            case "BACK":
                if (user.getSelectedTypeQuestions().equals("likedQuestions") && !isLiked)
                    schoolUserService.deleteLikedQuestionFromUser(questionId, testId, user);
                modelAndView = selectQuestions(testId, session, redirectAttributes);
                break;

            default:
                if (user.getSelectedTypeQuestions().equals("likedQuestions"))
                    schoolUserService.deleteLikedQuestionFromUser(questionId, testId, user);

                modelAndView = getTestToSolve(testId, user.getSelectedTypeQuestions(), session, redirectAttributes);

                if (user.getSelectedTypeQuestions().equals("likedQuestions") && isLiked)
                    schoolUserService.addLikedQuestionToUser(questionId, testId, user);
                break;
        }

        return modelAndView;
    }

    @PostMapping(value = {"/tests/resetTest"})
    public ModelAndView resetTest(@RequestParam("testId") Long testId, HttpSession session, RedirectAttributes redirectAttributes) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Test test = testService.getTestById(testId);
        if (test == null || !test.getDrivingCategory().equals(user.getCurrentCategory()))
            return displayTestsPage(session, redirectAttributes);
        List<StudentAnswersTest> answers = studentAnswersTestService.findAllBySchoolUserAndTest(user, test);
        if (answers.size() == test.getNumberQuestions())
            studentAnswersTestService.deleteAllStudentAnswersTest(answers);
        return selectQuestions(testId, session, redirectAttributes);
    }
}
