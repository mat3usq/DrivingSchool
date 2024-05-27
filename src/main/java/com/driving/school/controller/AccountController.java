package com.driving.school.controller;

import com.driving.school.model.Constants;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import com.driving.school.repository.StudentInstructorRepository;
import com.driving.school.service.SchoolUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {

    private final SchoolUserService schoolUserService;
    private final StudentInstructorRepository studentInstructorRepository;

    @Autowired
    public AccountController(SchoolUserService schoolUserService, StudentInstructorRepository studentInstructorRepository) {
        this.schoolUserService = schoolUserService;
        this.studentInstructorRepository = studentInstructorRepository;
    }

    @GetMapping("/account")
    public ModelAndView displayAccount(HttpSession session, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("account");

        if (authentication.getAuthorities().toArray()[0].toString().equals("ROLE_" + Constants.STUDENT_ROLE))
            modelAndView.addObject("instructorStudents", studentInstructorRepository.
                    findByStudentId(((SchoolUser) session.getAttribute("loggedInUser")).getId()));
        else if (authentication.getAuthorities().toArray()[0].toString().equals("ROLE_" + Constants.INSTRUCTOR_ROLE))
            modelAndView.addObject("instructorStudents", studentInstructorRepository.
                    findByInstructorId(((SchoolUser) session.getAttribute("loggedInUser")).getId()));

        modelAndView.addObject("instructors", schoolUserService.findAllInstructors());
        return modelAndView;
    }

    @PostMapping("/account/assignInstructor")
    public ModelAndView assignInstructor(@RequestParam("selectedInstructor") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/account");

        if (instructorId != null) {
            SchoolUser student = (SchoolUser) session.getAttribute("loggedInUser");
            SchoolUser instructor = schoolUserService.findUserById(instructorId);

            if (!studentInstructorRepository.existsByStudentAndInstructor(student, instructor)) {
                StudentInstructor studentInstructor = new StudentInstructor();
                studentInstructor.setStudent(student);
                studentInstructor.setInstructor(instructor);
                studentInstructor.setStatus(Constants.PENDING);
                studentInstructorRepository.save(studentInstructor);
            }
        }

        return modelAndView;
    }

    @PostMapping("/account/cancelInstructor")
    public ModelAndView cancelInstructor(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/account");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (loggedInUser.getId().equals(studentId)) {
            StudentInstructor studentInstructor = studentInstructorRepository.findByStudentIdAndInstructorId(studentId, instructorId);
            if (studentInstructor != null)
                studentInstructorRepository.delete(studentInstructor);
        }
        return modelAndView;
    }

    @PostMapping("/account/acceptStudent")
    public ModelAndView acceptStudent(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/account");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (loggedInUser.getId().equals(instructorId)) {
            StudentInstructor studentInstructor = studentInstructorRepository.findByStudentIdAndInstructorId(studentId, instructorId);
            studentInstructor.setStatus(Constants.ACTIVE);
            studentInstructorRepository.save(studentInstructor);
        }

        return modelAndView;
    }

    @PostMapping("/account/cancelStudent")
    public ModelAndView cancelStudent(@RequestParam("studentId") Long studentId, @RequestParam("instructorId") Long instructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/account");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (loggedInUser.getId().equals(instructorId)) {
            StudentInstructor studentInstructor = studentInstructorRepository.findByStudentIdAndInstructorId(studentId, instructorId);
            if (studentInstructor != null)
                studentInstructorRepository.delete(studentInstructor);
        }
        return modelAndView;
    }
}
