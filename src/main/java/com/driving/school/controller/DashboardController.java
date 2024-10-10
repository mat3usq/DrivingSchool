package com.driving.school.controller;

import com.driving.school.model.Constants;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.SchoolUserService;
import com.driving.school.service.StudentInstructorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {
    private final SchoolUserService schoolUserService;
    private final StudentInstructorService studentInstructorService;

    @Autowired
    public DashboardController(SchoolUserService schoolUserService, StudentInstructorService studentInstructorService) {
        this.schoolUserService = schoolUserService;
        this.studentInstructorService = studentInstructorService;
    }

    @GetMapping("/dashboard")
    public ModelAndView displayDashboard(Authentication authentication, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("dashboard");
        SchoolUser user = schoolUserService.findUserByEmail(authentication.getName());
        session.setAttribute("loggedInUser", user);

        if (user.getRoleName().equals(Constants.STUDENT_ROLE))
            modelAndView.addObject("instructorStudents", studentInstructorService.findByStudentId(user.getId()));
        else if (user.getRoleName().equals(Constants.INSTRUCTOR_ROLE))
            modelAndView.addObject("instructorStudents", studentInstructorService.findByInstructorId(user.getId()));

        modelAndView.addObject("instructors", schoolUserService.findAllInstructors());
        return modelAndView;
    }

    @PostMapping("/dashboard/changeCurrentCategory")
    public ModelAndView changeCategory(HttpSession session, @RequestParam("categoryId") Long categoryId) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        schoolUserService.changeCategory(user, categoryId);
        return new ModelAndView("dashboard");
    }

    @PostMapping("/dashboard/assignInstructor")
    public ModelAndView assignInstructor(@RequestParam("selectedInstructor") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        studentInstructorService.createStudentInstructor((SchoolUser) session.getAttribute("loggedInUser"), schoolUserService.findUserById(instructorId));
        return modelAndView;
    }

    @PostMapping("/dashboard/cancelInstructor")
    public ModelAndView cancelInstructor(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        if (loggedInUser.getId().equals(studentId))
            studentInstructorService.deleteStudentInstructor(studentId, instructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/acceptStudent")
    public ModelAndView acceptStudent(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        if (loggedInUser.getId().equals(instructorId))
            studentInstructorService.acceptStudent(studentId, instructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/cancelStudent")
    public ModelAndView cancelStudent(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        if (loggedInUser.getId().equals(instructorId))
            studentInstructorService.deleteStudentInstructor(studentId, instructorId);
        return modelAndView;
    }
}
