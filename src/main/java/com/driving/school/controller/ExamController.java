package com.driving.school.controller;


import com.driving.school.model.Exam;
import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentExam;
import com.driving.school.service.ExamService;
import com.driving.school.service.QuestionService;
import com.driving.school.service.StudentExamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ExamController {
    private final QuestionService questionService;
    private final ExamService examService;
    private final StudentExamService studentExamService;

    public ExamController(QuestionService questionService, ExamService examService,
                          StudentExamService studentExamService) {
        this.questionService = questionService;
        this.examService = examService;
        this.studentExamService = studentExamService;
    }

    @PostMapping("/exam/generate")
    public String generateExam(HttpSession session){
        List<Question> questionSet = generateQuestionSet();
        session.setAttribute("questionSet", questionSet);
        session.setAttribute("currentQuestion", Integer.valueOf(0));
        StudentExam studentExam = new StudentExam();
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        studentExam.setSchoolUser(user);
        Exam exam = new Exam();
        exam.setPoints(Long.valueOf("74"));
        exam.setName("B");
        exam = examService.createExam(exam);
        studentExam.setExam(exam);
        studentExam.setPoints(Long.valueOf("0"));
        studentExam = studentExamService.createStudentExam(studentExam);
        session.setAttribute("exam",studentExam);
        return "redirect:/exam/solve";
    }

    @GetMapping("/exam/solve")
    public ModelAndView examSolve(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("examSolve");
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");
        Integer currentQuestion = (Integer) session.getAttribute("currentQuestion");
        Question question = questionSet.get(currentQuestion);
        currentQuestion++;
        session.setAttribute("currentQuestion", currentQuestion);
        modelAndView.addObject("question",question);
        return modelAndView;
    }

    @GetMapping("/exam")
    public ModelAndView examInfo(){
        ModelAndView modelAndView = new ModelAndView("examInfo");
        return modelAndView;
    }

    @GetMapping("/exam/result")
    public ModelAndView summary(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("examResult");
        StudentExam studentExam = (StudentExam)session.getAttribute("exam");

        studentExam = studentExamService.getStudentExamById(studentExam.getId()).orElse(null);
        Long points = studentExam.getPoints();
        modelAndView.addObject("points",points);
        return modelAndView;
    }


    private List<Question> generateQuestionSet() {
        String category = "B";
        int numberOfNoSpecialistQuestions = 4;
       // int numberOfSpecialistQuestions = 12
        List<Question> noSpecialistQuestions = questionService.getRandomNoSpecialistcQuestionsByCategory(category, numberOfNoSpecialistQuestions);
     //   List<Question> specialistQuestions = questionService.getRandomSpecialistcQuestionsByCategory(category, numberOfSpecialistQuestions);

    //    noSpecialistQuestions.addAll(specialistQuestions);
        return noSpecialistQuestions;
    }

    @PostMapping(value = {"/exam/action"})
    public ModelAndView getActionFromExam(@RequestParam("questionId") Long questionId, @RequestParam("action") String action, HttpSession session) {

        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");
        Integer currentQuestion = (Integer) session.getAttribute("currentQuestion");
        StudentExam studentExam = (StudentExam) session.getAttribute("exam");
        studentExam = studentExamService.getStudentExamById(studentExam.getId()).orElse(null);
        Question question = questionService.findById(questionId).orElse(null);
        if(action.equals( question.getCorrectAnswer() )){
            studentExam.setPoints(studentExam.getPoints() + question.getPoints());
            studentExamService.updateStudentExam(studentExam.getId() ,studentExam);
        }
        ModelAndView modelAndView;
        if(questionSet.size() ==  currentQuestion){
            modelAndView = summary(session);

        }
        else{

            modelAndView = examSolve(session);
        }

        return modelAndView;
    }



}
