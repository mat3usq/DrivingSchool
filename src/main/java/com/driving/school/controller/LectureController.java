package com.driving.school.controller;

import com.driving.school.model.Lecture;
import com.driving.school.model.Subject;
import com.driving.school.model.Sublecture;
import com.driving.school.service.LectureService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class LectureController {
    private final LectureService lectureService;

    @Autowired
    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping(value = "/lecture")
    public ModelAndView displayLecturePage() {
        ModelAndView m = new ModelAndView("lecture");
        Lecture lecture = new Lecture();
        List<Sublecture> sublectures = IntStream.range(0, 10).mapToObj(i -> {
            Sublecture sublecture = new Sublecture();
            List<Subject> subjects = IntStream.range(0, 10).mapToObj(j -> new Subject()).toList();
            sublecture.setSubjects(subjects);
            return sublecture;
        }).toList();
        lecture.setSublectures(sublectures);
        m.addObject("newLecture", lecture);
        m.addObject("lectureList", lectureService.findAll());
        return m;
    }

    @PostMapping(value = "/lecture/addLecture")
    public String addLecture(@ModelAttribute("newLecture") Lecture lecture) {
        lecture.getSublectures().forEach(sublecture -> {
            sublecture.getSubjects().forEach(subject -> {
                if (subject != null && !subject.getFile().isEmpty()) {
                    try {
                        subject.setImage(subject.getFile().getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        lectureService.save(lecture);
        return "redirect:/lecture";
    }

    @GetMapping(value = "/lecture/editLecture/{id}")
    public ModelAndView redirectToEditLecture(@PathVariable("id") Long id, HttpSession session) {
        ModelAndView m = new ModelAndView("editLecture");
        Lecture editLecture = lectureService.findById(id);
        m.addObject("editLecture", editLecture);
        m.addObject("lectureList", lectureService.findAll());
        session.setAttribute("editLecture", editLecture);
        return m;
    }

    @PostMapping(value = "/lecture/editLecture")
    public String updateLecture(@ModelAttribute("lecture") Lecture lecture, HttpSession session) {
        Lecture editLecture = (Lecture) session.getAttribute("editLecture");
        editLecture.setName(lecture.getName());
        editLecture.setContent(lecture.getContent());
        lectureService.save(editLecture);
        return "redirect:/lecture";
    }

    @PostMapping(value = "/lecture/deleteLecture")
    public String deleteLecture(HttpSession session) {
        Lecture editLecture = (Lecture) session.getAttribute("editLecture");
        lectureService.delete(editLecture);
        return "redirect:/lecture";
    }
}
