package com.driving.school.controller;

import com.driving.school.model.Lecture;
import com.driving.school.model.Sublecture;
import com.driving.school.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class LectureController {
    LectureRepository lectureRepository;

    @Autowired
    public LectureController(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    @GetMapping(value = "/lecture")
    public ModelAndView displayLecturePage() {
        ModelAndView m = new ModelAndView("lecture");
        Lecture lecture = new Lecture();
        List<Sublecture> sublectures = IntStream.range(0, 10)
                .mapToObj(i -> new Sublecture())
                .collect(Collectors.toList());
        lecture.setSublectures(sublectures);
        m.addObject("newLecture", lecture);
        m.addObject("lectureList", lectureRepository.findAll());
        return m;
    }

    @PostMapping(value = "/lecture/addLecture")
    public String addLecture(@ModelAttribute("newLecture") Lecture lecture) {
        lectureRepository.save(lecture);
        lecture.getSublectures().forEach(s -> {
            System.out.println(s.getContent() + " " + s.getTitle());
        });
        return "redirect:/lecture";
    }
}
