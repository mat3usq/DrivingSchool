package com.driving.school.controller;


import com.driving.school.service.ExamService;
import com.driving.school.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ExamController {
    private final QuestionService questionService;
    private final ExamService examService;

    public ExamController(QuestionService questionService, ExamService examService) {
        this.questionService = questionService;
        this.examService = examService;
    }

//    @GetMapping("/exam/info")
//    public ModelAndView ExamInfo(){
//        ModelAndView modelAndView = new ModelAndView("ExamInfo");
//
//    }

    @GetMapping("/exam")
    public ModelAndView ExamInfo(){
        ModelAndView modelAndView = new ModelAndView("examInfo");
        List<String> categories = new ArrayList();
        categories.add("A");
        categories.add("B");
        categories.add("C");
        categories.add("D");
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }



}
