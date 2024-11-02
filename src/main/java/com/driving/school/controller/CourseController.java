package com.driving.school.controller;

import com.driving.school.model.*;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.repository.CourseRepository;
import com.driving.school.service.CourseService;
import com.driving.school.service.DrivingSessionService;
import com.driving.school.service.MentorShipService;
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
    private final MentorShipService mentorShipService;
    private final DashboardController dashboardController;
    private final DrivingSessionService drivingSessionService;
    private final CourseService courseService;

    @Autowired
    public CourseController(MentorShipService mentorShipService, DashboardController dashboardController, DrivingSessionService drivingSessionService, CourseService courseService) {
        this.mentorShipService = mentorShipService;
        this.dashboardController = dashboardController;
        this.drivingSessionService = drivingSessionService;
        this.courseService = courseService;
    }

    @PostMapping("/course/instructor/addCourse")
    public ModelAndView addCourse(@RequestParam("mentorShipId") Long mentorShipId, @ModelAttribute("newCourse") Course course, HttpSession session) {
        SchoolUser instructor = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> optMs = mentorShipService.getMentorShipById(mentorShipId);

        if (optMs.isPresent() && optMs.get().getInstructor().equals(instructor)) {
            course.setMentorShip(optMs.get());
            courseService.createCourse(course);
        }

        return dashboardController.showStudentForInstructor(mentorShipId, session);
    }

    @PostMapping("/course/instructor/editCourse")
    public ModelAndView editCourse(@RequestParam("courseId") Long courseId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Course> optionalCourse = courseService.getCourseById(courseId);

        if (optionalCourse.isPresent() && optionalCourse.get().getMentorShip().getInstructor().equals(user)) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("isEditCourse", true);
            modelAndView.addObject("availableCategories", optionalCourse.get().getMentorShip().getStudent().getAvailableCategories());
            modelAndView.addObject("course", optionalCourse.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/updateCourse")
    public ModelAndView updateCourse(@ModelAttribute("course") Course editedCourse, @RequestParam("courseId") Long courseId, HttpSession session) {
        Optional<Course> course = courseService.getCourseById(courseId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (course.isPresent() && course.get().getMentorShip().getInstructor().equals(user) ) {
            courseService.updateCourse(courseId, editedCourse);
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", course.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/deleteCourse")
    public ModelAndView deleteCourse(@RequestParam("courseId") Long courseId, HttpSession session) {
        SchoolUser instructor = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Course> optionalCourse = courseService.getCourseById(courseId);

        if (optionalCourse.isPresent() && optionalCourse.get().getMentorShip().getInstructor().equals(instructor)) {
            courseService.deleteCourse(optionalCourse.get().getId());
            return dashboardController.showStudentForInstructor(optionalCourse.get().getMentorShip().getId(), session);
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/showCourse")
    public ModelAndView showCourse(@RequestParam("courseId") Long courseId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Course> optionalCourse = courseService.getCourseById(courseId);

        if (optionalCourse.isPresent() && (
                optionalCourse.get().getMentorShip().getInstructor().equals(user) ||
                        optionalCourse.get().getMentorShip().getStudent().equals(user)
        )) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", optionalCourse.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());

            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/addDrivingSession")
    public ModelAndView addDrivingSession(@ModelAttribute("newDrivingSession") DrivingSession newDrivingSession,
                                          @RequestParam("courseId") Long courseId, HttpSession session) {
        Optional<Course> course = courseService.getCourseById(courseId);

        if (course.isPresent()) {
            drivingSessionService.createDrivingSession(newDrivingSession, course.get());
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", course.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/editDrivingSession")
    public ModelAndView editDrivingSession(@RequestParam("drivingSessionId") Long drivingSessionId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<DrivingSession> ds = drivingSessionService.getDrivingSessionById(drivingSessionId);

        if (ds.isPresent() && ds.get().getCourse().getMentorShip().getInstructor().equals(user)) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("isEditDrivingSession", true);
            modelAndView.addObject("editDrivingSession", ds.get());
            modelAndView.addObject("course", ds.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/updateDrivingSession")
    public ModelAndView updateDrivingSession(@ModelAttribute("editDrivingSession") DrivingSession editDrivingSession,
                                             @RequestParam("editDrivingSessionId") Long editDrivingSessionId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<DrivingSession> ds = drivingSessionService.getDrivingSessionById(editDrivingSessionId);

        if (ds.isPresent() && ds.get().getCourse().getMentorShip().getInstructor().equals(user)) {
            drivingSessionService.updateDrivingSession(editDrivingSessionId, editDrivingSession);
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", ds.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/deleteDrivingSession")
    public ModelAndView deleteDrivingSession(@RequestParam("drivingSessionId") Long drivingSessionId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<DrivingSession> ds = drivingSessionService.getDrivingSessionById(drivingSessionId);

        if (ds.isPresent() && ds.get().getCourse().getMentorShip().getInstructor().equals(user)) {
            drivingSessionService.deleteDrivingSession(drivingSessionId);
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", ds.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }
}
