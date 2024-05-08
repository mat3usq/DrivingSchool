package com.driving.school.controller;

import com.driving.school.model.Lecture;
import com.driving.school.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

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
        m.addObject("newLecture", new Lecture());
        m.addObject("lectureList", lectureRepository.findAll());
        return m;
    }

    @PostMapping(value = "/lecture/addLecture")
    public String addLecture(@ModelAttribute("newLecture") Lecture lecture) {
        lectureRepository.save(lecture);
        return "redirect:/lecture";
    }
}
