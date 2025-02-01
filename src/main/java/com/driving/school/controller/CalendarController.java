package com.driving.school.controller;

import com.driving.school.model.*;
import com.driving.school.repository.InstructionEventRepository;
import com.driving.school.service.InstructorEventService;
import com.driving.school.service.MentorShipService;
import com.driving.school.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Controller
@RequestMapping("/calendar")
public class CalendarController {
    private final InstructionEventRepository instructionEventRepository;
    private final InstructorEventService instructorEventService;
    private final MentorShipService mentorShipService;
    private final NotificationService notificationService;

    @Autowired
    public CalendarController(InstructionEventRepository instructionEventRepository, InstructorEventService instructorEventService,
                              MentorShipService mentorShipService, NotificationService notificationService) {
        this.instructionEventRepository = instructionEventRepository;
        this.instructorEventService = instructorEventService;
        this.mentorShipService = mentorShipService;
        this.notificationService = notificationService;
    }

    @GetMapping("")
    public ModelAndView displayCalendar(@RequestParam(required = false) Integer month,
                                        @RequestParam(required = false) Integer year,
                                        HttpSession session, Authentication auth) {
        YearMonth yearMonth = (month == null || year == null) ? YearMonth.now() : YearMonth.of(year, month);
        return getCalendarModelAndView(yearMonth, LocalDateTime.now(), session, auth);
    }

    @PostMapping("")
    public ModelAndView displayCalendarForNextMonth(@RequestParam(required = false) String date,
                                                    @RequestParam(required = false) Integer month,
                                                    HttpSession session, Authentication auth) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(date, formatter).plusMonths(month);
        } catch (Exception e) {
            localDateTime = LocalDateTime.now();
        }
        YearMonth yearMonth = YearMonth.from(localDateTime);
        return getCalendarModelAndView(yearMonth, localDateTime, session, auth);
    }

    private ModelAndView getCalendarModelAndView(YearMonth yearMonth, LocalDateTime localDateTime, HttpSession session, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("calendar");
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        List<InstructionEvent> events = instructorEventService.getEvents(user, yearMonth, authentication);
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE", new Locale("pl"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("pl"));
        String formattedDay = localDateTime.format(dayFormatter);
        String formattedDate = localDateTime.format(dateFormatter);

        modelAndView.addObject("formattedDay", formattedDay);
        modelAndView.addObject("formattedDate", formattedDate);
        modelAndView.addObject("events", events);
        modelAndView.addObject("date", localDateTime);
        modelAndView.addObject("newEvent", new InstructionEvent());
        modelAndView.addObject("eventTypes", Constants.getAllEventTypes());

        return modelAndView;
    }

    @PostMapping("/student/assignEvent")
    public ModelAndView signUpForEvent(@RequestParam("eventId") Long eventId, HttpSession session, Authentication authentication) {
        InstructionEvent event = instructorEventService.findById(eventId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (event != null)
            if (mentorShipService.existsByStudentAndInstructor(user, event.getInstructor()))
                if (event.getAvailableEventSlots() != 0)
                    if (event.getStudents().stream().noneMatch(s -> Objects.equals(s.getId(), user.getId()))) {
                        instructorEventService.addStudentToInstructionEvent(eventId, user.getId());
                        event.setAvailableEventSlots(event.getAvailableEventSlots() - 1);
                        instructionEventRepository.save(event);
                        try {
                            notificationService.scheduleReminderForEvent(event, user);
                        } catch (SchedulerException e) {
                            System.out.println("Scheduler Exception" + e.getMessage());
                        }
                    }

        return getCalendarModelAndView(YearMonth.from(event.getStartTime()), event.getStartTime(), session, authentication);
    }

    @PostMapping("/student/leaveEvent")
    public ModelAndView leaveEvent(@RequestParam("eventId") Long eventId, HttpSession session, Authentication authentication) {
        InstructionEvent event = instructorEventService.findById(eventId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (event != null)
            if (mentorShipService.existsByStudentAndInstructor(user, event.getInstructor()))
                if (event.getStudents().stream().anyMatch(s -> Objects.equals(s.getId(), user.getId()))) {
                    instructorEventService.removeStudentFromInstructionEvent(eventId, user.getId());
                    instructionEventRepository.save(event);
                    notificationService.cancelReminderForEvent(eventId, user.getId());
                }

        return getCalendarModelAndView(YearMonth.from(event.getStartTime()), event.getStartTime(), session, authentication);
    }

    @PostMapping("/operation/addEvent")
    public ModelAndView addEvent(@Valid @ModelAttribute InstructionEvent newEvent, Errors errors, HttpSession session, Authentication authentication) {
        if (newEvent.getStartTime() == null)
            return displayCalendar(YearMonth.now().getMonth().getValue(), YearMonth.now().getYear(), session, authentication);

        YearMonth yearMonth = YearMonth.from(newEvent.getStartTime());
        newEvent.setInstructor((SchoolUser) session.getAttribute("loggedInUser"));
        newEvent.setAvailableEventSlots(newEvent.getEventCapacity());

        if (errors.hasErrors() && ((SchoolUser) session.getAttribute("loggedInUser")).getRoleName().equals(Constants.INSTRUCTOR_ROLE)) {
            ModelAndView modelAndView = getCalendarModelAndView(yearMonth, newEvent.getStartTime(), session, authentication);
            modelAndView.addObject("newEvent", newEvent);
            modelAndView.addObject("org.springframework.validation.BindingResult.newEvent", errors);
            modelAndView.addObject("createEventValidationInfo", "Wprowadź poprawne dane, aby dodac wydarzenie!");
            return modelAndView;
        }

        instructionEventRepository.save(newEvent);
        notificationService.sendNotificationWhenInstructorCreateNewEvent(newEvent);
        return getCalendarModelAndView(yearMonth, newEvent.getStartTime(), session, authentication);
    }

    @PostMapping("/operation/deleteEvent")
    public ModelAndView deleteEvent(@RequestParam("eventId") Long eventId, HttpSession session, Authentication authentication) {
        InstructionEvent event = instructorEventService.findById(eventId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (event != null)
            if (user.getId().equals(event.getInstructor().getId()) || user.getRoleName().equals(Constants.ADMIN_ROLE)) {
                instructionEventRepository.delete(event);
                event.getStudents().forEach(s -> notificationService.cancelReminderForEvent(eventId, s.getId()));
            }

        return getCalendarModelAndView(YearMonth.from(event.getStartTime()), event.getStartTime(), session, authentication);
    }

    @GetMapping("/operation/editEvent")
    public ModelAndView displayEditEvent(@RequestParam("eventId") Long eventId, HttpSession session, Authentication authentication) {
        InstructionEvent event = instructorEventService.findById(eventId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (user.getId().equals(event.getInstructor().getId()) || user.getRoleName().equals(Constants.ADMIN_ROLE)) {
            session.setAttribute("eventToEdit", event);
            ModelAndView modelAndView = new ModelAndView("editEventCalendar");
            modelAndView.addObject("formattedDay", event.getStartTime().format(DateTimeFormatter.ofPattern("EEE", new Locale("pl"))));
            modelAndView.addObject("formattedDate", event.getStartTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("pl"))));
            modelAndView.addObject("editedEvent", event);
            modelAndView.addObject("eventTypes", Constants.getAllEventTypes());
            return modelAndView;
        } else
            return getCalendarModelAndView(YearMonth.from(event.getStartTime()), event.getStartTime(), session, authentication);
    }

    @PostMapping("/operation/editEvent")
    public ModelAndView editEvent(@Valid @ModelAttribute("editedEvent") InstructionEvent editedEvent, Errors errors, HttpSession session, Authentication authentication) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        InstructionEvent event = (InstructionEvent) session.getAttribute("eventToEdit");

        if (event != null && (user.getId().equals(event.getInstructor().getId()) || user.getRoleName().equals(Constants.ADMIN_ROLE))) {
            if (errors.hasErrors() && user.getRoleName().equals(Constants.INSTRUCTOR_ROLE)) {
                editedEvent.setEventCapacity(event.getEventCapacity());
                editedEvent.setAvailableEventSlots(event.getAvailableEventSlots());
                editedEvent.setStudents(event.getStudents());
                ModelAndView modelAndView = displayEditEvent(event.getId(), session, authentication);
                modelAndView.addObject("editedEvent", editedEvent);
                modelAndView.addObject("editEventValidationInfo", "Wprowadź poprawne dane, aby zaktualizowac wydarzenie!");
                return modelAndView;
            }

            instructorEventService.updateInstructionEvent(event.getId(), editedEvent);
        }

        return getCalendarModelAndView(YearMonth.from(editedEvent.getStartTime()), editedEvent.getStartTime(), session, authentication);
    }

    @PostMapping("/operation/deleteUserFromEvent")
    public ModelAndView deleteStudentFromEvent(@RequestParam("eventId") Long eventId, @RequestParam("studentId") Long studentId, HttpSession session, Authentication authentication) {
        InstructionEvent event = instructorEventService.findById(eventId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (event != null && user.getId().equals(event.getInstructor().getId()) || user.getRoleName().equals(Constants.ADMIN_ROLE)) {
            instructorEventService.removeStudentFromInstructionEvent(eventId, studentId);
            notificationService.cancelReminderForEvent(eventId, studentId);
        }

        return getCalendarModelAndView(YearMonth.from(event.getStartTime()), event.getStartTime(), session, authentication);
    }
}
