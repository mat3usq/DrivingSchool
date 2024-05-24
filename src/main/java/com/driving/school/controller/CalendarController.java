package com.driving.school.controller;

import com.driving.school.model.Constants;
import com.driving.school.model.InstructionEvent;
import com.driving.school.repository.InstructionEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

@Controller
public class CalendarController {
    private final InstructionEventRepository instructionEventRepository;

    @Autowired
    public CalendarController(InstructionEventRepository instructionEventRepository) {
        this.instructionEventRepository = instructionEventRepository;
    }

    @GetMapping("/calendar")
    public ModelAndView displayCalendar(@RequestParam(required = false) Integer month,
                                        @RequestParam(required = false) Integer year) {
        YearMonth yearMonth = (month == null || year == null) ? YearMonth.now() : YearMonth.of(year, month);
        return getCalendarModelAndView(yearMonth, LocalDateTime.now());
    }

    @PostMapping("/calendar")
    public ModelAndView displayCalendarForNextMonth(@RequestParam(required = false) String date,
                                                    @RequestParam(required = false) Integer month) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter).plusMonths(month);
        YearMonth yearMonth = YearMonth.from(localDateTime);
        return getCalendarModelAndView(yearMonth, localDateTime);
    }

    @PostMapping("/calendar/addEvent")
    public ModelAndView addEvent(@ModelAttribute InstructionEvent event) {
        instructionEventRepository.save(event);
        YearMonth yearMonth = YearMonth.from(event.getStartTime());
        return getCalendarModelAndView(yearMonth, event.getStartTime());
    }

    private ModelAndView getCalendarModelAndView(YearMonth yearMonth, LocalDateTime localDateTime) {
        ModelAndView modelAndView = new ModelAndView("calendar");
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        List<InstructionEvent> events = instructionEventRepository.findByStartTimeBetween(startOfMonth, endOfMonth);

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
}
