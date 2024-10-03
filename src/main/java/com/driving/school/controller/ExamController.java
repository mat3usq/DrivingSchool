package com.driving.school.controller;

import com.driving.school.model.*;
import com.driving.school.service.QuestionService;
import com.driving.school.service.StudentExamAnswerService;
import com.driving.school.service.StudentExamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ExamController {
    private final QuestionService questionService;
    private final StudentExamService studentExamService;
    private final StudentExamAnswerService studentExamAnswerService;

    @Autowired
    public ExamController(QuestionService questionService, StudentExamService studentExamService, StudentExamAnswerService studentExamAnswerService) {
        this.questionService = questionService;
        this.studentExamService = studentExamService;
        this.studentExamAnswerService = studentExamAnswerService;
    }

    @GetMapping("/exam")
    public ModelAndView examInfo() {
        return new ModelAndView("instructionExam");
    }

    @PostMapping("/exam/generate")
    public ModelAndView generateExam(HttpSession session) {
        String category = "B";
        ModelAndView modelAndView = new ModelAndView();

        List<Question> questionSet = studentExamService.generateQuestionSet(category);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        StudentExam studentExam = new StudentExam();
        studentExam.setSchoolUser(user);
        studentExam.setCategory(category);
        studentExam.setPoints(Long.valueOf("0"));
        studentExam = studentExamService.createStudentExam(studentExam);

        session.setAttribute("questionSet", questionSet);
        session.setAttribute("exam", studentExam);

        modelAndView.setViewName("redirect:/exam/solve");
        return modelAndView;
    }

    @GetMapping("/exam/solve")
    public ModelAndView examSolve(HttpSession session) {
        if (session.getAttribute("questionSet") == null)
            return examInfo();

        ModelAndView modelAndView = new ModelAndView("solveExam");
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");

        if (!questionSet.isEmpty()) {
            Question question = questionSet.getFirst();
            questionSet.remove(question);
            modelAndView.addObject("question", question);
        }

        int maxQuestions = 32;
        int remainingQuestions = maxQuestions - questionSet.size();

        int noSpecCounter;
        int specCounter = 0;

        if (remainingQuestions <= 20)
            noSpecCounter = remainingQuestions;
        else {
            noSpecCounter = 20;
            specCounter = remainingQuestions - 20;
        }

        modelAndView.addObject("noSpecCounter", noSpecCounter);
        modelAndView.addObject("specCounter", specCounter);

        return modelAndView;
    }


    @PostMapping(value = {"/exam/action"})
    public ModelAndView getActionFromExam(@RequestParam("questionId") Long questionId, @RequestParam("action") String action, HttpSession session) {
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");
        StudentExam studentExam = (StudentExam) session.getAttribute("exam");

        studentExam = studentExamService.getStudentExamById(studentExam.getId());
        Question question = questionService.findById(questionId).orElse(null);

        StudentExamAnswer studentExamAnswer = new StudentExamAnswer();
        studentExamAnswer.setStudentExam(studentExam);
        studentExamAnswer.setQuestion(question);
        studentExamAnswer.setAnswer(action);
        studentExamAnswer.setCorrectness(false);

        if (question != null && action.equals(question.getCorrectAnswer())) {
            studentExam.setPoints(studentExam.getPoints() + question.getPoints());
            studentExamAnswer.setCorrectness(true);
            studentExamService.updateStudentExam(studentExam.getId(), studentExam);
        }

        studentExamAnswerService.save(studentExamAnswer);

        ModelAndView modelAndView;
        if (questionSet.isEmpty())
            modelAndView = summary(session);
        else
            modelAndView = examSolve(session);

        return modelAndView;
    }

    @GetMapping("/exam/result")
    public ModelAndView summary(HttpSession session) {
        if (session.getAttribute("questionSet") != null && ((List<Question>) session.getAttribute("questionSet")).isEmpty()) {
            ModelAndView modelAndView = new ModelAndView("examResult");
            StudentExam studentExam = (StudentExam) session.getAttribute("exam");

            studentExam = studentExamService.getStudentExamById(studentExam.getId());
            Long points = studentExam.getPoints();
            modelAndView.addObject("points", points);

            session.setAttribute("questionSet", null);
            session.setAttribute("exam", null);
            return modelAndView;
        } else return examSolve(session);
    }
}