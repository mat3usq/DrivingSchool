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
        ModelAndView modelAndView = new ModelAndView();

        if (session.getAttribute("currentQuestionExam") == null) {
            List<Question> questionSet = studentExamService.generateQuestionSet();
            SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
            StudentExam studentExam = new StudentExam();

            studentExam.setSchoolUser(user);
            studentExam.setCategory("B");
            studentExam.setPoints(Long.valueOf("0"));
            studentExam = studentExamService.createStudentExam(studentExam);

            session.setAttribute("questionSet", questionSet);
            session.setAttribute("currentQuestionExam", 1);
            session.setAttribute("exam", studentExam);

            System.out.println(studentExam);
        }

        modelAndView.setViewName("redirect:/exam/solve");
        return modelAndView;
    }

    @GetMapping("/exam/solve")
    public ModelAndView examSolve(HttpSession session) {
        if (session.getAttribute("currentQuestionExam") == null)
            return generateExam(session);

        ModelAndView modelAndView = new ModelAndView("solveExam");
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");
        Integer currentQuestionExam = (Integer) session.getAttribute("currentQuestionExam");
        Question question = questionSet.get(currentQuestionExam - 1);
        currentQuestionExam++;
        session.setAttribute("currentQuestionExam", currentQuestionExam);
        modelAndView.addObject("question", question);

        return modelAndView;
    }

    @PostMapping(value = {"/exam/action"})
    public ModelAndView getActionFromExam(@RequestParam("questionId") Long questionId, @RequestParam("action") String action, HttpSession session) {
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");
        Integer currentQuestionExam = (Integer) session.getAttribute("currentQuestionExam");
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
        if (questionSet.size() == currentQuestionExam) {
            session.setAttribute("currentQuestionExam", -1);
            modelAndView = summary(session);
        } else
            modelAndView = examSolve(session);

        return modelAndView;
    }

    @GetMapping("/exam/result")
    public ModelAndView summary(HttpSession session) {
        if ((Integer) session.getAttribute("currentQuestionExam") == -1) {
            ModelAndView modelAndView = new ModelAndView("examResult");
            StudentExam studentExam = (StudentExam) session.getAttribute("exam");

            studentExam = studentExamService.getStudentExamById(studentExam.getId());
            Long points = studentExam.getPoints();
            modelAndView.addObject("points", points);

            session.setAttribute("currentQuestionExam", null);
            session.setAttribute("questionSet", null);
            session.setAttribute("exam", null);
            return modelAndView;
        } else return new ModelAndView("redirect:/exam/solve");
    }
}