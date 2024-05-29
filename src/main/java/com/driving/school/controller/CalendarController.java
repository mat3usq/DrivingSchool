package com.driving.school.controller;

import com.driving.school.model.Constants;
import com.driving.school.model.InstructionEvent;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import com.driving.school.repository.InstructionEventRepository;
import com.driving.school.service.InstructorEventService;
import com.driving.school.service.StudentInstructorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
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
public class CalendarController {
    private final InstructionEventRepository instructionEventRepository;
    private final InstructorEventService instructorEventService;
    private final StudentInstructorService studentInstructorService;

    @Autowired
    public CalendarController(InstructionEventRepository instructionEventRepository, InstructorEventService instructorEventService,
                              StudentInstructorService studentInstructorService) {
        this.instructionEventRepository = instructionEventRepository;
        this.instructorEventService = instructorEventService;
        this.studentInstructorService = studentInstructorService;
    }

    @GetMapping("/calendar")
    public ModelAndView displayCalendar(@RequestParam(required = false) Integer month,
                                        @RequestParam(required = false) Integer year,
                                        HttpSession session, Authentication auth) {
        YearMonth yearMonth = (month == null || year == null) ? YearMonth.now() : YearMonth.of(year, month);
        return getCalendarModelAndView(yearMonth, LocalDateTime.now(), session, auth);
    }

    @PostMapping("/calendar")
    public ModelAndView displayCalendarForNextMonth(@RequestParam(required = false) String date,
                                                    @RequestParam(required = false) Integer month,
                                                    HttpSession session, Authentication auth) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter).plusMonths(month);
        YearMonth yearMonth = YearMonth.from(localDateTime);
        return getCalendarModelAndView(yearMonth, localDateTime, session, auth);
    }

    @PostMapping("/calendar/addEvent")
    public ModelAndView addEvent(@ModelAttribute InstructionEvent event, HttpSession session, Authentication authentication) {
        event.setInstructor((SchoolUser) session.getAttribute("loggedInUser"));
        if (event.getEventCapacity() < 0)
            event.setEventCapacity(0);
        event.setCurrentEventCapacity(event.getEventCapacity());
        instructionEventRepository.save(event);
        YearMonth yearMonth = YearMonth.from(event.getStartTime());
        return getCalendarModelAndView(yearMonth, event.getStartTime(), session, authentication);
    }

    private ModelAndView getCalendarModelAndView(YearMonth yearMonth, LocalDateTime localDateTime, HttpSession session, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("calendar");
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        List<InstructionEvent> events = new ArrayList<>();
        if (authentication.getAuthorities().toArray()[0].toString().equals("ROLE_STUDENT")) {
            List<StudentInstructor> studentInstructors = studentInstructorService.findByStudentId(user.getId());
            for (StudentInstructor si : studentInstructors) {
                if (si.getStatus().equals(Constants.ACTIVE)) {
                    List<InstructionEvent> eventsByInstructor = instructorEventService.findInstructionEventsByTimeRangeAndInstructor(startOfMonth, endOfMonth, si.getInstructor().getId());
                    events.addAll(eventsByInstructor);
                }
            }
        } else if (authentication.getAuthorities().toArray()[0].toString().equals("ROLE_INSTRUCTOR")) {
            events = instructorEventService.findInstructionEventsByTimeRangeAndInstructor(startOfMonth, endOfMonth, user.getId());
        } else if (authentication.getAuthorities().toArray()[0].toString().equals("ROLE_ADMIN"))
            events = instructorEventService.findInstructionEventsByTimeRange(startOfMonth, endOfMonth);

        events.forEach(e -> {
            if (e.getStudents() != null)
                e.setIsAssigned(e.getStudents().stream().anyMatch(s -> Objects.equals(s.getId(), user.getId())));
        });

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

    @PostMapping("/calendar/assignEvent")
    public ModelAndView signUpForEvent(@RequestParam("eventId") Long eventId, HttpSession session, Authentication authentication) {
        InstructionEvent event = instructorEventService.findById(eventId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (studentInstructorService.existsByStudentAndInstructor(user, event.getInstructor()))
            if (event.getCurrentEventCapacity() != 0)
                if (event.getStudents().stream().noneMatch(s -> Objects.equals(s.getId(), user.getId()))) {
                    instructorEventService.addStudentToInstructionEvent(eventId, user.getId());
                    event.setCurrentEventCapacity(event.getCurrentEventCapacity() - 1);
                    instructionEventRepository.save(event);
                }

        return getCalendarModelAndView(YearMonth.from(event.getStartTime()), event.getStartTime(), session, authentication);
    }

    @PostMapping("/calendar/leaveEvent")
    public ModelAndView leaveEvent(@RequestParam("eventId") Long eventId, HttpSession session, Authentication authentication) {
        InstructionEvent event = instructorEventService.findById(eventId);
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (studentInstructorService.existsByStudentAndInstructor(user, event.getInstructor()))
            if (event.getStudents().stream().anyMatch(s -> Objects.equals(s.getId(), user.getId()))) {
                instructorEventService.removeStudentFromInstructionEvent(eventId, user.getId());
                event.setCurrentEventCapacity(event.getCurrentEventCapacity() + 1);
                instructionEventRepository.save(event);
            }

        return getCalendarModelAndView(YearMonth.from(event.getStartTime()), event.getStartTime(), session, authentication);
    }
}
