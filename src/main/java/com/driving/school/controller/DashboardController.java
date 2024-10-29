package com.driving.school.controller;

import com.driving.school.model.*;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.service.SchoolUserService;
import com.driving.school.service.MentorShipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class DashboardController {
    private final SchoolUserService schoolUserService;
    private final MentorShipService mentorShipService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public DashboardController(SchoolUserService schoolUserService, MentorShipService mentorShipService, CategoryRepository categoryRepository) {
        this.schoolUserService = schoolUserService;
        this.mentorShipService = mentorShipService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/dashboard")
    public ModelAndView displayDashboard(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("dashboard");
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        switch (user.getRoleName()) {
            case Constants.STUDENT_ROLE ->
                    modelAndView.addObject("mentorShips", mentorShipService.findByStudentId(user.getId()));
            case Constants.INSTRUCTOR_ROLE -> {
                Page<MentorShip> rel = mentorShipService.findByInstructorId(user.getId(), PageRequest.of(0, size));
                int totalPages = rel.getTotalPages();
                page = (page < 0) ? 0 : (page >= totalPages && totalPages > 0) ? totalPages - 1 : page;
                rel = mentorShipService.findByInstructorId(user.getId(), PageRequest.of(page, size));
                modelAndView.addObject("mentorShips", rel.getContent());
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
        mentorShipService.createMentorShipWithStatus((SchoolUser) session.getAttribute("loggedInUser"), schoolUserService.findUserById(instructorId), Constants.PENDING);
        return modelAndView;
    }

    @PostMapping("/dashboard/student/cancelInstructor")
    public ModelAndView cancelInstructor(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getStudent().getId()))
            mentorShipService.deleteMentorShipById(mentorShipId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/assignStudent")
    public ModelAndView assignStudent(@RequestParam("studentEmail") String studentEmail, HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser studentUser = schoolUserService.findUserByEmail(studentEmail);
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (studentUser != null && loggedInUser.getRoleName().equals(Constants.INSTRUCTOR_ROLE) && mentorShipService.createMentorShipWithStatus(studentUser, loggedInUser, Constants.ACTIVE))
            redirectAttributes.addFlashAttribute("assignStudentInfo", "Pomyślnie dodano studenta!");
        else
            redirectAttributes.addFlashAttribute("assignStudentInfo", "Nie udało się dodać studenta.");

        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/acceptStudent")
    public ModelAndView acceptStudent(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId()))
            mentorShipService.acceptStudent(mentorShipId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/cancelStudent")
    public ModelAndView cancelStudent(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId()))
            mentorShipService.deleteMentorShipById(mentorShipId);
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/finishStudent")
    public ModelAndView finishStudent(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId()))
            mentorShipService.finishMentorShip(mentorShipId);
        return modelAndView;
    }

    @PostMapping("/dashboard/admin/searchUser")
    public ModelAndView userDetailsByEmail(@RequestParam("userEmail") String userEmail) {
        SchoolUser user = schoolUserService.findUserByEmail(userEmail);
        if (user != null)
            return getUserDetails(user, new ModelAndView("schoolUserDetails"));
        return new ModelAndView("redirect:/dashboard");
    }

    @PostMapping("/dashboard/admin/userDetails")
    public ModelAndView userDetailsByUserId(@RequestParam("userId") Long userId) {
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null)
            return getUserDetails(user, new ModelAndView("schoolUserDetails"));
        return new ModelAndView("redirect:/dashboard");
    }

    private ModelAndView getUserDetails(SchoolUser user, ModelAndView modelAndView) {
        modelAndView.addObject("user", user);
        modelAndView.addObject("newPayment", new Payment());
        modelAndView.addObject("allCategories", categoryRepository.findAll());
        return modelAndView;
    }

    @PostMapping("/dashboard/admin/addPayment")
    public ModelAndView addPayment(@RequestParam("userId") Long userId, @ModelAttribute("newPayment") Payment payment) {
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null) {
            schoolUserService.addPayment(userId, payment);
            return getUserDetails(user, new ModelAndView("schoolUserDetails"));
        }
        return userDetailsByUserId(userId);
    }

    @PostMapping("/dashboard/admin/deletePayment")
    public ModelAndView deletePayment(@RequestParam("userId") Long userId, @RequestParam("paymentId") Long paymentId) {
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null) {
            schoolUserService.deletePayment(paymentId);
            return getUserDetails(user, new ModelAndView("schoolUserDetails"));
        }
        return userDetailsByUserId(userId);
    }

    @PostMapping("/dashboard/instructor/studentDetails")
    public ModelAndView showStudentForInstructor(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId())) {
            SchoolUser user = schoolUserService.findUserById(ms.get().getStudent().getId());
            if (user != null) {
                ModelAndView model = getUserDetails(user, new ModelAndView("schoolUserDetails"));
                model.addObject("mentorShip", ms.get());
                model.addObject("newCourse", new Course());
                return model;
            }
        }

        return new ModelAndView("redirect:/dashboard");
    }
}
