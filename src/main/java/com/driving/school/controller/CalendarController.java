package com.driving.school.controller;

import com.driving.school.model.InstructionEvent;
import com.driving.school.repository.InstructionEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

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
        ModelAndView modelAndView = new ModelAndView("calendar");

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        if (month == null || year == null) {
            YearMonth currentMonth = YearMonth.now();
            startDateTime = currentMonth.atDay(1).atStartOfDay();
            endDateTime = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        } else {
            YearMonth specifiedMonth = YearMonth.of(year, month);
            startDateTime = specifiedMonth.atDay(1).atStartOfDay();
            endDateTime = specifiedMonth.atEndOfMonth().atTime(23, 59, 59);
        }

        List<InstructionEvent> events = instructionEventRepository.findByStartTimeBetween(startDateTime, endDateTime);
        modelAndView.addObject("events", events);

        return modelAndView;
    }
}
