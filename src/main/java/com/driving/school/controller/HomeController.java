package com.driving.school.controller;

import com.driving.school.model.Constants;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.repository.QuestionRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.repository.StudentExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private final StudentExamRepository studentExamRepository;
    private final QuestionRepository questionRepository;
    private final SchoolUserRepository schoolUserRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public HomeController(StudentExamRepository studentExamRepository, QuestionRepository questionRepository, SchoolUserRepository schoolUserRepository, CategoryRepository categoryRepository) {
        this.studentExamRepository = studentExamRepository;
        this.questionRepository = questionRepository;
        this.schoolUserRepository = schoolUserRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping(value = {"", "/", "/home"})
    public ModelAndView displayHomePage() {
        ModelAndView m = new ModelAndView("home");
        m.addObject("registerUser", new SchoolUser());
        m.addObject("numberOfStudentExams", studentExamRepository.count());
        m.addObject("numberOfAvailableQuestions", questionRepository.count());
        m.addObject("numberOfSchoolUsers", schoolUserRepository.count());
        m.addObject("numberOfCategories", categoryRepository.count());
        m.addObject("numberOfInstructors", schoolUserRepository.countAllByRoleName(Constants.INSTRUCTOR_ROLE));
        return m;
    }
}
