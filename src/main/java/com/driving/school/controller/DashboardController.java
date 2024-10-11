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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        switch (user.getRoleName()) {
            case Constants.STUDENT_ROLE ->
                    modelAndView.addObject("instructorStudents", studentInstructorService.findByStudentId(user.getId()));
            case Constants.INSTRUCTOR_ROLE ->
                    modelAndView.addObject("instructorStudents", studentInstructorService.findByInstructorId(user.getId()));
            case Constants.ADMIN_ROLE -> modelAndView.addObject("schoolUsers", schoolUserService.findAllUsers());
        }

        modelAndView.addObject("instructors", schoolUserService.findAllInstructors());
        return modelAndView;
    }

    @PostMapping("/dashboard/changeCurrentCategory")
    public ModelAndView changeCategory(HttpSession session, @RequestParam("categoryId") Long categoryId) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        schoolUserService.changeCategory(user, categoryId);
        return new ModelAndView("redirect:/dashboard");
    }

    @PostMapping("/dashboard/student/assignInstructor")
    public ModelAndView assignInstructor(@RequestParam("selectedInstructor") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        studentInstructorService.createStudentInstructorWithStatus((SchoolUser) session.getAttribute("loggedInUser"), schoolUserService.findUserById(instructorId), Constants.PENDING);
        return modelAndView;
    }

    @PostMapping("/dashboard/student/cancelInstructor")
    public ModelAndView cancelInstructor(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        if (loggedInUser.getId().equals(studentId))
            studentInstructorService.deleteStudentInstructor(studentId, instructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/assignStudent")
    public ModelAndView assignStudent(@RequestParam("studentEmail") String studentEmail, HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser studentUser = schoolUserService.findUserByEmail(studentEmail);
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (studentUser != null && loggedInUser.getRoleName().equals(Constants.INSTRUCTOR_ROLE)) {
            studentInstructorService.createStudentInstructorWithStatus(studentUser, loggedInUser, Constants.ACTIVE);
            redirectAttributes.addFlashAttribute("assignStudentInfo", "Pomyślnie dodano studenta!");
        } else {
            redirectAttributes.addFlashAttribute("assignStudentInfo", "Nieprawidłowy email.");
        }

        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/acceptStudent")
    public ModelAndView acceptStudent(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        if (loggedInUser.getId().equals(instructorId))
            studentInstructorService.acceptStudent(studentId, instructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/cancelStudent")
    public ModelAndView cancelStudent(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        if (loggedInUser.getId().equals(instructorId))
            studentInstructorService.deleteStudentInstructor(studentId, instructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/finishStudent")
    public ModelAndView finishStudent(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        if (loggedInUser.getId().equals(instructorId))
            studentInstructorService.finishStudentInstructor(studentId, instructorId);
        return modelAndView;
    }
}
