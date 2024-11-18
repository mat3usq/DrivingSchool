package com.driving.school.controller;

import com.driving.school.model.*;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.repository.CourseRepository;
import com.driving.school.service.NotificationService;
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

import java.util.Objects;
import java.util.Optional;

@Controller
public class DashboardController {
    private final SchoolUserService schoolUserService;
    private final MentorShipService mentorShipService;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public DashboardController(SchoolUserService schoolUserService, MentorShipService mentorShipService, CategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.schoolUserService = schoolUserService;
        this.mentorShipService = mentorShipService;
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    // FOR ALL USERS

    @GetMapping("/dashboard")
    public ModelAndView displayDashboard(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("dashboard");
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        switch (user.getRoleName()) {
            case Constants.STUDENT_ROLE -> {
                modelAndView.addObject("mentorShips", mentorShipService.findByStudentId(user.getId()));
                modelAndView.addObject("instructors", schoolUserService.findAllInstructors());
            }
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

        return modelAndView;
    }

    @PostMapping("/dashboard/changeCurrentCategory")
    public ModelAndView changeCategory(HttpSession session, @RequestParam("categoryId") Long categoryId) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        schoolUserService.changeCategory(user, categoryId);
        return new ModelAndView("redirect:/dashboard");
    }

    public ModelAndView getUserDetails(SchoolUser user, ModelAndView modelAndView) {
        modelAndView.addObject("user", user);
        modelAndView.addObject("newPayment", new Payment());
        modelAndView.addObject("allCategories", categoryRepository.findAll());
        modelAndView.addObject("newCourse", new Course());
        if (Objects.equals(user.getRoleName(), Constants.INSTRUCTOR_ROLE))
            modelAndView.addObject("mentorShips", mentorShipService.findByInstructorId(user.getId()));
        if (Objects.equals(user.getRoleName(), Constants.STUDENT_ROLE))
            modelAndView.addObject("mentorShips", mentorShipService.findByStudentId(user.getId()));

        return modelAndView;
    }

    // FOR STUDENTS

    @PostMapping("/dashboard/student/instructorDetails")
    public ModelAndView showInstructorForStudent(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getStudent().getId())) {
            SchoolUser user = schoolUserService.findUserById(ms.get().getInstructor().getId());
            if (user != null) {
                ModelAndView model = getUserDetails(user, new ModelAndView("schoolUserDetails"));
                model.addObject("courses", courseRepository.findByMentorShipId(mentorShipId));
                model.addObject("mentorShip", ms.get());
                return model;
            }
        }

        return new ModelAndView("redirect:/dashboard");
    }

    @PostMapping("/dashboard/student/assignInstructor")
    public ModelAndView assignInstructor(@RequestParam(value = "selectedInstructor", required = false) Long instructorId, HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard#instructorsDetails");
        SchoolUser student = (SchoolUser) session.getAttribute("loggedInUser");
        if (instructorId != null) {
            SchoolUser instructor = schoolUserService.findUserById(instructorId);
            if (instructor != null && mentorShipService.studentAssignsToInstructor(student, instructor))
                redirectAttributes.addFlashAttribute("assignInstructorInfo", "Pomyślnie udało się zacząć wspołpracę z instruktorem!");
            else
                redirectAttributes.addFlashAttribute("assignInstructorInfo", "Nie udało się zacząć wspołpracy z instruktorem!");
        } else
            redirectAttributes.addFlashAttribute("assignInstructorInfo", "Nie udało się zacząć wspołpracy z instruktorem!");

        return modelAndView;
    }

    @PostMapping("/dashboard/student/cancelInstructor")
    public ModelAndView cancelInstructor(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard#instructorsDetails");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getStudent().getId()))
            mentorShipService.studentCancelMentorshipWithInstructor(ms.get());
        return modelAndView;
    }

    // FOR INSTRUCTORS

    @PostMapping("/dashboard/instructor/studentDetails")
    public ModelAndView showStudentForInstructor(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId())) {
            SchoolUser user = schoolUserService.findUserById(ms.get().getStudent().getId());
            if (user != null) {
                ModelAndView model = getUserDetails(user, new ModelAndView("schoolUserDetails"));
                model.addObject("courses", courseRepository.findByMentorShipId(mentorShipId));
                model.addObject("mentorShip", ms.get());
                return model;
            }
        }

        return new ModelAndView("redirect:/dashboard#studentsDetails");
    }

    @PostMapping("/dashboard/instructor/assignStudent")
    public ModelAndView assignStudent(@RequestParam("studentEmail") String studentEmail, HttpSession session, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard#studentsDetails");
        SchoolUser studentUser = schoolUserService.findUserByEmail(studentEmail);
        SchoolUser instructorUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (studentUser != null && instructorUser.getRoleName().equals(Constants.INSTRUCTOR_ROLE))
            if (mentorShipService.instructorCreateMentorshipWithStudent(studentUser, instructorUser))
                redirectAttributes.addFlashAttribute("assignStudentInfo", "Pomyślnie udało się zacząć wspołprace ze studentem!");
            else
                redirectAttributes.addFlashAttribute("assignStudentInfo", "Nie udało się zacząć wspołpracy ze studentem!");
        else redirectAttributes.addFlashAttribute("assignStudentInfo", "Nie udało się zacząć wspołpracy ze studentem!");

        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/acceptStudent")
    public ModelAndView acceptStudent(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard#studentsDetails");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId()))
            mentorShipService.instructorAcceptMentorshipWithStudent(ms.get());
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/cancelStudent")
    public ModelAndView cancelStudent(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard#studentsDetails");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId()))
            mentorShipService.instructorCancelMentorshipWithStudent(ms.get());
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/finishStudent")
    public ModelAndView finishStudent(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard#studentsDetails");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId()))
            mentorShipService.instructorFinishMentorshipWithStudent(ms.get());
        return modelAndView;
    }

    @PostMapping("/dashboard/instructor/backToActiveMentorShip")
    public ModelAndView backToActiveMentorShip(@RequestParam("mentorShipId") Long mentorShipId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard#studentsDetails");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId()))
            mentorShipService.instructorBackToActiveMentorshipWithStudent(ms.get());
        return modelAndView;
    }

    // FOR ADMIN

    @PostMapping("/dashboard/admin/searchUser")
    public ModelAndView userDetailsByEmail(@RequestParam("userEmail") String userEmail, RedirectAttributes redirectAttributes) {
        SchoolUser user = schoolUserService.findUserByEmail(userEmail);
        if (user != null)
            return getUserDetails(user, new ModelAndView("schoolUserDetails"));

        redirectAttributes.addFlashAttribute("assignUserInfo", "Nie znaleziono uzytkownika!");
        return new ModelAndView("redirect:/dashboard#usersDetails");
    }

    @PostMapping("/dashboard/admin/userDetails")
    public ModelAndView userDetailsByUserId(@RequestParam("userId") Long userId) {
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null)
            return getUserDetails(user, new ModelAndView("schoolUserDetails"));
        return new ModelAndView("redirect:/dashboard#usersDetails");
    }

    @PostMapping("/dashboard/admin/addPayment")
    public ModelAndView addPayment(@RequestParam("userId") Long userId, @ModelAttribute("newPayment") Payment payment) {
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null)
            schoolUserService.addPayment(userId, payment);

        return userDetailsByUserId(userId);
    }

    @PostMapping("/dashboard/admin/deletePayment")
    public ModelAndView deletePayment(@RequestParam("userId") Long userId, @RequestParam("paymentId") Long paymentId) {
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null)
            schoolUserService.deletePayment(paymentId);

        return userDetailsByUserId(userId);
    }

    @PostMapping("/dashboard/admin/promoteUser")
    public ModelAndView promoteUser(@RequestParam("userId") Long userId) {
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null)
            schoolUserService.promoteUser(user);

        return userDetailsByUserId(userId);
    }

    @PostMapping("/dashboard/admin/demoteUser")
    public ModelAndView demoteUser(@RequestParam("userId") Long userId) {
        SchoolUser user = schoolUserService.findUserById(userId);
        if (user != null)
            schoolUserService.demoteUser(user);

        return userDetailsByUserId(userId);
    }

    @PostMapping("/dashboard/admin/assignSchoolUser")
    public ModelAndView assignSchoolUser(@RequestParam("parentUserMail") String parentUserMail, @RequestParam("userMail") String userMail, RedirectAttributes redirectAttributes) {
        SchoolUser parentUser = schoolUserService.findUserByEmail(parentUserMail);
        SchoolUser user = schoolUserService.findUserByEmail(userMail);

        if (parentUser != null && user != null) {
            if (parentUser.getRoleName().equals(Constants.INSTRUCTOR_ROLE) && user.getRoleName().equals(Constants.STUDENT_ROLE))
                mentorShipService.createMentorShipWithStatus(user, parentUser, Constants.ACTIVE);
            else if (user.getRoleName().equals(Constants.INSTRUCTOR_ROLE) && parentUser.getRoleName().equals(Constants.STUDENT_ROLE))
                mentorShipService.createMentorShipWithStatus(parentUser, user, Constants.ACTIVE);

            redirectAttributes.addFlashAttribute("assignUserInfo", "Pomyslnie zawarto wspolprace miedzy uzytkownikami!");
        } else redirectAttributes.addFlashAttribute("assignUserInfo", "Nie znaleziono uzytkownika!");

        return userDetailsByEmail(parentUserMail, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/cancelMentorShip")
    public ModelAndView cancelMentorShip(@RequestParam("mentorShipId") Long mentorShipId, @RequestParam("parentUserMail") String parentUserMail, RedirectAttributes redirectAttributes) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent())
            mentorShipService.deleteMentorShipById(mentorShipId);

        return userDetailsByEmail(parentUserMail, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/acceptMentorShip")
    public ModelAndView acceptMentorShip(@RequestParam("mentorShipId") Long mentorShipId, @RequestParam("parentUserMail") String parentUserMail, RedirectAttributes redirectAttributes) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent())
            mentorShipService.acceptStudent(mentorShipId);

        return userDetailsByEmail(parentUserMail, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/finishMentorShip")
    public ModelAndView finishMentorShip(@RequestParam("mentorShipId") Long mentorShipId, @RequestParam("parentUserMail") String parentUserMail, RedirectAttributes redirectAttributes) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent())
            mentorShipService.finishMentorShip(mentorShipId);

        return userDetailsByEmail(parentUserMail, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/backToActiveMentorShip")
    public ModelAndView backToActiveMentorShip(@RequestParam("mentorShipId") Long mentorShipId, @RequestParam("parentUserMail") String parentUserMail, RedirectAttributes redirectAttributes) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        if (ms.isPresent())
            mentorShipService.backToActiveMentorShip(mentorShipId);

        return userDetailsByEmail(parentUserMail, redirectAttributes);
    }

    @PostMapping("/dashboard/admin/userCourseDetails")
    public ModelAndView showUserCourseDetails(@RequestParam("mentorShipId") Long mentorShipId, @RequestParam("parentUserMail") String parentUserMail, RedirectAttributes redirectAttributes) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        SchoolUser user = schoolUserService.findUserByEmail(parentUserMail);

        if (ms.isPresent() && user != null) {
            ModelAndView model = getUserDetails(user, new ModelAndView("schoolUserDetails"));
            model.addObject("courses", courseRepository.findByMentorShipId(mentorShipId));
            model.addObject("mentorShip", ms.get());
            model.addObject("seeWhoseCourseIs", true);
            if (user.getRoleName().equals(Constants.INSTRUCTOR_ROLE))
                model.addObject("descSeeWhoseCourseIs", "Kursy stworzone dla uzytkownika: " + ms.get().getStudent().getName());
            else if (user.getRoleName().equals(Constants.STUDENT_ROLE))
                model.addObject("descSeeWhoseCourseIs", "Kursy stworzone przez uzytkownika: " + ms.get().getInstructor().getName());

            return model;
        }

        return userDetailsByEmail(parentUserMail, redirectAttributes);
    }
}
