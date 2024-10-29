package com.driving.school.controller;

import com.driving.school.model.Course;
import com.driving.school.model.MentorShip;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.repository.CourseRepository;
import com.driving.school.service.MentorShipService;
import com.driving.school.service.SchoolUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class CourseController {
    private final SchoolUserService schoolUserService;
    private final MentorShipService mentorShipService;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    private final DashboardController dashboardController;

    @Autowired
    public CourseController(SchoolUserService schoolUserService, MentorShipService mentorShipService, CategoryRepository categoryRepository, CourseRepository courseRepository, DashboardController dashboardController) {
        this.schoolUserService = schoolUserService;
        this.mentorShipService = mentorShipService;
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.dashboardController = dashboardController;
    }

    @PostMapping("/course/instructor/addCourse")
    public ModelAndView addCourse(@RequestParam("mentorShipId") Long mentorShipId, @ModelAttribute("newCourse") Course course, HttpSession session) {
        SchoolUser instructor = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> optMs = mentorShipService.getMentorShipById(mentorShipId);

        if (optMs.isPresent() && optMs.get().getInstructor().equals(instructor)) {
            course.setMentorShip(optMs.get());
            courseRepository.save(course);
        }

        return dashboardController.showStudentForInstructor(mentorShipId, session);
    }
}
