package com.driving.school.controller;

import com.driving.school.model.*;
import com.driving.school.repository.CommentCourseRepository;
import com.driving.school.repository.CourseRepository;
import com.driving.school.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class CourseController {
    private final MentorShipService mentorShipService;
    private final DashboardController dashboardController;
    private final DrivingSessionService drivingSessionService;
    private final CourseService courseService;
    private final TestCourseService testCourseService;
    private final CommentCourseRepository commentCourseRepository;
    private final CourseRepository courseRepository;
    private final SchoolUserService schoolUserService;

    @Autowired
    public CourseController(MentorShipService mentorShipService, DashboardController dashboardController, DrivingSessionService drivingSessionService, CourseService courseService, TestCourseService testCourseService, CommentCourseRepository commentCourseRepository, CourseRepository courseRepository, SchoolUserService schoolUserService) {
        this.mentorShipService = mentorShipService;
        this.dashboardController = dashboardController;
        this.drivingSessionService = drivingSessionService;
        this.courseService = courseService;
        this.testCourseService = testCourseService;
        this.commentCourseRepository = commentCourseRepository;
        this.courseRepository = courseRepository;
        this.schoolUserService = schoolUserService;
    }

    // Course Mappings

    @PostMapping("/course/showCourse")
    public ModelAndView showCourse(@RequestParam("courseId") Long courseId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Course> optionalCourse = courseService.getCourseById(courseId);

        if (optionalCourse.isPresent() && (
                optionalCourse.get().getMentorShip().getInstructor().equals(user) ||
                        optionalCourse.get().getMentorShip().getStudent().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE)
        )) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", optionalCourse.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());

            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @GetMapping(value = "/course/instructor/courseCreateValidation")
    public ModelAndView catchCreateCourseValidation(@RequestParam Long mentorShipId, @Valid @ModelAttribute("newCourse") Course course, Errors errors, HttpSession session) {
        Optional<MentorShip> ms = mentorShipService.getMentorShipById(mentorShipId);
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");

        if (ms.isPresent() && loggedInUser.getId().equals(ms.get().getInstructor().getId())) {
            SchoolUser user = schoolUserService.findUserById(ms.get().getStudent().getId());
            if (user != null) {
                ModelAndView model = dashboardController.getUserDetails(user, new ModelAndView("schoolUserDetails"));
                model.addObject("courses", courseRepository.findByMentorShipId(mentorShipId));
                model.addObject("mentorShip", ms.get());
                model.addObject("newCourse", course);
                model.addObject("createCourseValidationInfo", "Wprowadź poprawne dane, aby dodać kurs!");
                return model;
            }
        }

        return new ModelAndView("redirect:/dashboard#studentsDetails");
    }


    @PostMapping("/course/instructor/addCourse")
    public ModelAndView addCourse(@RequestParam("mentorShipId") Long mentorShipId, @Valid @ModelAttribute("newCourse") Course course, Errors errors, HttpSession session, RedirectAttributes redirectAttributes) {
        SchoolUser instructor = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<MentorShip> optMs = mentorShipService.getMentorShipById(mentorShipId);

        if (optMs.isPresent() && optMs.get().getInstructor().equals(instructor)) {
            if (errors.hasErrors()) {
                redirectAttributes.addAttribute("mentorShipId", mentorShipId);
                redirectAttributes.addFlashAttribute("newCourse", course);
                return new ModelAndView("redirect:/course/instructor/courseCreateValidation#createCourse");
            }
            course.setMentorShip(optMs.get());
            courseService.instructorCreateNewCourse(course);
        }

        return dashboardController.showStudentForInstructor(mentorShipId, session);
    }


    @PostMapping("/course/instructor/editCourse")
    public ModelAndView editCourse(@RequestParam("courseId") Long courseId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Course> optionalCourse = courseService.getCourseById(courseId);

        if (optionalCourse.isPresent() &&
                (optionalCourse.get().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("isEditCourse", true);
            modelAndView.addObject("availableCategories", optionalCourse.get().getMentorShip().getStudent().getAvailableCategories());
            modelAndView.addObject("course", optionalCourse.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());
            modelAndView.addObject("newCommentCourse", new CommentCourse());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/updateCourse")
    public ModelAndView updateCourse(@Valid @ModelAttribute("course") Course editedCourse, Errors errors, @RequestParam("courseId") Long courseId, @ModelAttribute("newCommentCourse") CommentCourse newCommentCourse, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Course> optionalCourse = courseService.getCourseById(courseId);

        if (optionalCourse.isPresent() &&
                (optionalCourse.get().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {

            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", optionalCourse.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());

            if (errors.hasErrors() && user.getRoleName().equals(Constants.INSTRUCTOR_ROLE)) {
                editedCourse.setId(optionalCourse.get().getId());
                editedCourse.setCommentCourses(optionalCourse.get().getCommentCourses());
                editedCourse.setTestCourses(optionalCourse.get().getTestCourses());
                editedCourse.setDrivingSessions(optionalCourse.get().getDrivingSessions());
                modelAndView.addObject("isEditCourse", true);
                modelAndView.addObject("course", editedCourse);
                modelAndView.addObject("availableCategories", optionalCourse.get().getMentorShip().getStudent().getAvailableCategories());
                modelAndView.addObject("newCommentCourse", new CommentCourse());
                modelAndView.addObject("editCourseValidationInfo", "Wprowadź poprawne dane, aby zaktualizowac kurs!");
                return modelAndView;
            }

            courseService.updateCourse(courseId, editedCourse, newCommentCourse);
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

    @PostMapping("/course/admin/addCourse")
    public ModelAndView addCourse(@RequestParam("mentorShipId") Long mentorShipId, @ModelAttribute("newCourse") Course course, @RequestParam("parentUserMail") String parentUserMail, RedirectAttributes redirectAttributes) {
        Optional<MentorShip> optMs = mentorShipService.getMentorShipById(mentorShipId);

        if (optMs.isPresent()) {
            course.setMentorShip(optMs.get());
            courseService.createCourse(course);
        }

        return dashboardController.showUserCourseDetails(mentorShipId, parentUserMail, redirectAttributes);
    }

    @PostMapping("/course/admin/deleteCourse")
    public ModelAndView deleteCourseByAdmin(@RequestParam("courseId") Long courseId, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<Course> optionalCourse = courseService.getCourseById(courseId);

        if (optionalCourse.isPresent()) {
            courseService.deleteCourse(optionalCourse.get().getId());
            return dashboardController.showUserCourseDetails(optionalCourse.get().getMentorShip().getId(), optionalCourse.get().getMentorShip().getInstructor().getEmail(), redirectAttributes);
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    // Driving Sessions mappings

    @PostMapping("/course/instructor/addDrivingSession")
    public ModelAndView addDrivingSession(@Valid @ModelAttribute("newDrivingSession") DrivingSession newDrivingSession, Errors errors,
                                          @RequestParam("courseId") Long courseId, HttpSession session) {
        Optional<Course> course = courseService.getCourseById(courseId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (course.isPresent() && (
                course.get().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE)
        )) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", course.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());

            if (user.getRoleName().equals(Constants.ADMIN_ROLE))
                drivingSessionService.createDrivingSession(newDrivingSession, course.get());
            else {
                if (errors.hasErrors()) {
                    modelAndView.addObject("newDrivingSession", newDrivingSession);
                    modelAndView.addObject("createDrivingSessionValidationInfo", "Wprowadź poprawne dane, aby dodać sesje jazdy!");
                    return modelAndView;
                }
                drivingSessionService.instructorCreateDrivingSession(newDrivingSession, course.get());
            }
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/editDrivingSession")
    public ModelAndView editDrivingSession(@RequestParam("drivingSessionId") Long drivingSessionId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<DrivingSession> ds = drivingSessionService.getDrivingSessionById(drivingSessionId);

        if (ds.isPresent() && (
                ds.get().getCourse().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("isEditDrivingSession", true);
            modelAndView.addObject("editDrivingSession", ds.get());
            modelAndView.addObject("course", ds.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/updateDrivingSession")
    public ModelAndView updateDrivingSession(@Valid @ModelAttribute("editDrivingSession") DrivingSession editDrivingSession, Errors errors,
                                             @RequestParam("editDrivingSessionId") Long editDrivingSessionId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<DrivingSession> ds = drivingSessionService.getDrivingSessionById(editDrivingSessionId);

        if (ds.isPresent() && (
                ds.get().getCourse().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", ds.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());

            if (errors.hasErrors() && user.getRoleName().equals(Constants.INSTRUCTOR_ROLE)) {
                modelAndView.addObject("isEditDrivingSession", true);
                editDrivingSession.setId(editDrivingSessionId);
                editDrivingSession.setSessionDate(ds.get().getSessionDate());
                modelAndView.addObject("editDrivingSession", editDrivingSession);
                modelAndView.addObject("editDrivingSessionValidationInfo", "Wprowadź poprawne dane, aby zaktualizowac sesje jazdy!");
                return modelAndView;
            }

            drivingSessionService.updateDrivingSession(editDrivingSessionId, editDrivingSession);
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/deleteDrivingSession")
    public ModelAndView deleteDrivingSession(@RequestParam("drivingSessionId") Long drivingSessionId, HttpSession
            session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<DrivingSession> ds = drivingSessionService.getDrivingSessionById(drivingSessionId);

        if (ds.isPresent() && (
                ds.get().getCourse().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            drivingSessionService.deleteDrivingSession(drivingSessionId);
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", ds.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    // Results Test in Course mappings

    @PostMapping("/course/instructor/addTestCourse")
    public ModelAndView addTestCourse(@Valid @ModelAttribute("newTestCourse") TestCourse newTestCourse, Errors errors,
                                      @RequestParam("courseId") Long courseId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Course> course = courseService.getCourseById(courseId);

        if (course.isPresent() && (
                course.get().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", course.get());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());

            if (user.getRoleName().equals(Constants.ADMIN_ROLE))
                testCourseService.createTestCourse(newTestCourse, course.get());
            else {
                if (errors.hasErrors()) {
                    modelAndView.addObject("newTestCourse", newTestCourse);
                    modelAndView.addObject("createTestCourseValidationInfo", "Wprowadź poprawne dane, aby dodać wynik testu!");
                    return modelAndView;
                }
                testCourseService.instructorCreateTestCourse(newTestCourse, course.get());
            }
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/editTestCourse")
    public ModelAndView editTestCourse(@RequestParam("testCourseId") Long testCourseId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<TestCourse> testCourse = testCourseService.getTestCourseById(testCourseId);

        if (testCourse.isPresent() && (
                testCourse.get().getCourse().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("isEditTestCourse", true);
            modelAndView.addObject("editTestCourse", testCourse.get());
            modelAndView.addObject("course", testCourse.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/updateTestCourse")
    public ModelAndView updateTestCourse(@Valid @ModelAttribute("editTestCourse") TestCourse editTestCourse, Errors errors,
                                         @RequestParam("editTestCourseId") Long editTestCourseId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<TestCourse> tc = testCourseService.getTestCourseById(editTestCourseId);

        if (tc.isPresent() && (tc.get().getCourse().getMentorShip().getInstructor().equals(user)
                || user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", tc.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());

            if (errors.hasErrors() && user.getRoleName().equals(Constants.INSTRUCTOR_ROLE)) {
                modelAndView.addObject("isEditTestCourse", true);
                editTestCourse.setId(editTestCourseId);
                editTestCourse.setTestDate(tc.get().getTestDate());
                modelAndView.addObject("editTestCourse", editTestCourse);
                modelAndView.addObject("editDrivingSessionValidationInfo", "Wprowadź poprawne dane, aby zaktualizowac wynik testu!");
                return modelAndView;
            }

            testCourseService.updateTestCourse(editTestCourseId, editTestCourse);
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/deleteTestCourse")
    public ModelAndView deleteTestCourse(@RequestParam("testCourseId") Long testCourseId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<TestCourse> testCourse = testCourseService.getTestCourseById(testCourseId);

        if (testCourse.isPresent() && (
                testCourse.get().getCourse().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            testCourseService.deleteTestCourse(testCourseId);
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", testCourse.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }

    @PostMapping("/course/instructor/deleteCommentCourse")
    public ModelAndView deleteCommentCourse(@RequestParam("commentCourseId") Long commentCourseId, HttpSession
            session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<CommentCourse> commentCourse = commentCourseRepository.findById(commentCourseId);

        if (commentCourse.isPresent() && (
                commentCourse.get().getCourse().getMentorShip().getInstructor().equals(user) ||
                        user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            commentCourseRepository.delete(commentCourse.get());
            ModelAndView modelAndView = new ModelAndView("courseDetails");
            modelAndView.addObject("course", commentCourse.get().getCourse());
            modelAndView.addObject("newDrivingSession", new DrivingSession());
            modelAndView.addObject("newTestCourse", new TestCourse());
            return modelAndView;
        }

        return dashboardController.displayDashboard(0, 10, session);
    }
}
