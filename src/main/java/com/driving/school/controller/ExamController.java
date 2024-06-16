package com.driving.school.controller;


import com.driving.school.service.ExamService;
import com.driving.school.service.QuestionService;
import org.springframework.stereotype.Controller;

@Controller
public class ExamController {
    private final QuestionService questionService;
    private final ExamService examService;

    public ExamController(QuestionService questionService, ExamService examService) {
        this.questionService = questionService;
        this.examService = examService;
    }



}
