package com.driving.school.controller;


import com.driving.school.model.*;
import com.driving.school.service.ExamService;
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
    private final ExamService examService;
    private final StudentExamService studentExamService;
    private final StudentExamAnswerService studentExamAnswerService;

    @Autowired
    public ExamController(QuestionService questionService, ExamService examService, StudentExamService studentExamService,
    StudentExamAnswerService studentExamAnswerService) {
        this.questionService = questionService;
        this.examService = examService;
        this.studentExamService = studentExamService;
        this.studentExamAnswerService = studentExamAnswerService;
    }

    @GetMapping("/exam")
    public ModelAndView examInfo() {
        return new ModelAndView("instructionExam");
    }

    @PostMapping("/exam/generate")
    public String generateExam(HttpSession session) {
        List<Question> questionSet = generateQuestionSet();
        session.setAttribute("questionSet", questionSet);
        session.setAttribute("currentQuestion", 0);
        StudentExam studentExam = new StudentExam();
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        studentExam.setSchoolUser(user);
        Exam exam = new Exam();
        exam.setPoints(Long.valueOf("74"));
        exam.setName("B");
        exam = examService.createExam(exam);
        studentExam.setExam(exam);
        studentExam.setPoints(Long.valueOf("0"));
        studentExam.setCategory("B");
        studentExam = studentExamService.createStudentExam(studentExam);
        session.setAttribute("exam", studentExam);
        System.out.println(studentExam);
        return "redirect:/exam/solve";
    }

    @GetMapping("/exam/solve")
    public ModelAndView examSolve(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("solveExam");
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");
        Integer currentQuestion = (Integer) session.getAttribute("currentQuestion");
        Question question = questionSet.get(currentQuestion);
        currentQuestion++;
        session.setAttribute("currentQuestion", currentQuestion);
        modelAndView.addObject("question", question);
        return modelAndView;
    }

    @GetMapping("/exam/result")
    public ModelAndView summary(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("examResult");
        StudentExam studentExam = (StudentExam) session.getAttribute("exam");

        studentExam = studentExamService.getStudentExamById(studentExam.getId());
        Long points = studentExam.getPoints();
        modelAndView.addObject("points", points);
        return modelAndView;
    }

    @PostMapping(value = {"/exam/action"})
    public ModelAndView getActionFromExam(@RequestParam("questionId") Long questionId, @RequestParam("action") String action, HttpSession session) {
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");
        Integer currentQuestion = (Integer) session.getAttribute("currentQuestion");
        StudentExam studentExam = (StudentExam) session.getAttribute("exam");
        System.out.println(studentExam);
        studentExam = studentExamService.getStudentExamById(studentExam.getId());
        Question question = questionService.findById(questionId).orElse(null);
        if(studentExam==null){
        System.out.println(studentExam);}
        else{
            System.out.println("jest nulem");
        }
        StudentExamAnswer studentExamAnswer = new StudentExamAnswer();
        studentExamAnswer.setStudentExam(studentExam);
        studentExamAnswer.setQuestion(question);
        studentExamAnswer.setAnswer(action);
        studentExamAnswer.setCorrectness(false);
        studentExamAnswer.setOrder(currentQuestion);



        if (action.equals(question.getCorrectAnswer())) {
            studentExam.setPoints(studentExam.getPoints() + question.getPoints());
            studentExamService.updateStudentExam(studentExam.getId(), studentExam);
            studentExamAnswer.setCorrectness(true);
        }

        studentExamAnswerService.save(studentExamAnswer);

        ModelAndView modelAndView;
        if (questionSet.size() == currentQuestion)
            modelAndView = summary(session);
        else
            modelAndView = examSolve(session);

        return modelAndView;
    }

    private List<Question> generateQuestionSet() {
        String category = "B";
        int numberOfNoSpecialistQuestions = 4;
//        int numberOfSpecialistQuestions = 12;
        List<Question> noSpecialistQuestions = questionService.getRandomNoSpecialistcQuestionsByCategory(category, numberOfNoSpecialistQuestions);
//        List<Question> specialistQuestions = questionService.getRandomSpecialistcQuestionsByCategory(category, numberOfSpecialistQuestions);

//        noSpecialistQuestions.addAll(specialistQuestions);
        return noSpecialistQuestions;
    }
}