package com.driving.school.controller;

import com.driving.school.model.Constants;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.SchoolUserService;
import com.driving.school.service.StudentInstructorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

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
    public ModelAndView displayDashboard(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         Authentication authentication, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("dashboard");
        SchoolUser user = schoolUserService.findUserByEmail(authentication.getName());
        session.setAttribute("loggedInUser", user);

        switch (user.getRoleName()) {
            case Constants.STUDENT_ROLE ->
                    modelAndView.addObject("instructorStudents", studentInstructorService.findByStudentId(user.getId()));
            case Constants.INSTRUCTOR_ROLE -> {
                Page<StudentInstructor> rel = studentInstructorService.findByInstructorId(user.getId(), PageRequest.of(0, size));
                int totalPages = rel.getTotalPages();
                page = (page < 0) ? 0 : (page >= totalPages && totalPages > 0) ? totalPages - 1 : page;
                rel = studentInstructorService.findByInstructorId(user.getId(), PageRequest.of(page, size));
                modelAndView.addObject("instructorStudents", rel.getContent());
                modelAndView.addObject("currentPage", page);
                modelAndView.addObject("totalPages", totalPages);
            }
            case Constants.ADMIN_ROLE -> {
                Page<SchoolUser> userPage = schoolUserService.findAllUsers(PageRequest.of(0, size));
                int totalPages = userPage.getTotalPages();
                page = (page < 0) ? 0 : (page >= totalPages && totalPages > 0) ? totalPages - 1 : page;
                userPage = schoolUserService.findAllUsers(PageRequest.of(page, size));
                modelAndView.addObject("schoolUsers", userPage.getContent());
                modelAndView.addObject("currentPage", page);
                modelAndView.addObject("totalPages", totalPages);
            }
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
    public ModelAndView cancelInstructor(@RequestParam("studentInstructorId") Long studentInstructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<StudentInstructor> si = studentInstructorService.getStudentInstructorById(studentInstructorId);
        if (si.isPresent() && loggedInUser.getId().equals(si.get().getStudent().getId()))
            studentInstructorService.deleteStudentInstructor(studentInstructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/assignStudent")
    public ModelAndView assignStudent(@RequestParam("studentEmail") String studentEmail, HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser studentUser = schoolUserService.findUserByEmail(studentEmail);
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (studentUser != null && loggedInUser.getRoleName().equals(Constants.INSTRUCTOR_ROLE) && studentInstructorService.createStudentInstructorWithStatus(studentUser, loggedInUser, Constants.ACTIVE))
            redirectAttributes.addFlashAttribute("assignStudentInfo", "Pomyślnie dodano studenta!");
        else
            redirectAttributes.addFlashAttribute("assignStudentInfo", "Nie udało się dodać studenta.");

        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/acceptStudent")
    public ModelAndView acceptStudent(@RequestParam("studentInstructorId") Long studentInstructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<StudentInstructor> si = studentInstructorService.getStudentInstructorById(studentInstructorId);
        if (si.isPresent() && loggedInUser.getId().equals(si.get().getInstructor().getId()))
            studentInstructorService.acceptStudent(studentInstructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/cancelStudent")
    public ModelAndView cancelStudent(@RequestParam("studentInstructorId") Long studentInstructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<StudentInstructor> si = studentInstructorService.getStudentInstructorById(studentInstructorId);
        if (si.isPresent() && loggedInUser.getId().equals(si.get().getInstructor().getId()))
            studentInstructorService.deleteStudentInstructor(studentInstructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/finishStudent")
    public ModelAndView finishStudent(@RequestParam("studentInstructorId") Long studentInstructorId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<StudentInstructor> si = studentInstructorService.getStudentInstructorById(studentInstructorId);
        if (si.isPresent() && loggedInUser.getId().equals(si.get().getInstructor().getId()))
            studentInstructorService.finishStudentInstructor(studentInstructorId);
        return modelAndView;
    }

    @PostMapping("/dashboard/admin/userDetails")
    public ModelAndView userDetails(@RequestParam("userId") Long userId) {
        ModelAndView modelAndView = new ModelAndView("schoolUserDetails");
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null)
            modelAndView.addObject("user", user);
        return modelAndView;
    }
}
