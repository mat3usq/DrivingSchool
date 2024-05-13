package com.driving.school.controller;

import com.driving.school.model.Lecture;
import com.driving.school.model.Subject;
import com.driving.school.model.Sublecture;
import com.driving.school.repository.SublectureRepository;
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
    private final SublectureRepository sublectureRepository;

    @Autowired
    public LectureController(LectureService lectureService, SublectureRepository sublectureRepository) {
        this.lectureService = lectureService;
        this.sublectureRepository = sublectureRepository;
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
        Lecture editedLecture = lectureService.findById(id);
        m.addObject("newLecture", editedLecture);
        m.addObject("lectureList", lectureService.findAll());
        session.setAttribute("editedLecture", editedLecture);
        return m;
    }

    @PostMapping(value = "/lecture/editLecture")
    public String updateLecture(@ModelAttribute("newLecture") Lecture lecture, HttpSession session) {
        Lecture editedLecture = (Lecture) session.getAttribute("editedLecture");
        editedLecture.setName(lecture.getName());
        editedLecture.setContent(lecture.getContent());
        lectureService.save(editedLecture);
        return "redirect:/lecture";
    }

    @PostMapping(value = "/lecture/deleteLecture")
    public String deleteLecture(HttpSession session) {
        Lecture editedLecture = (Lecture) session.getAttribute("editedLecture");
        lectureService.delete(editedLecture);
        return "redirect:/lecture";
    }

    @GetMapping(value = "/lecture/editSublecture/{id}")
    public ModelAndView redirectToEditSublecture(@PathVariable("id") Long id, HttpSession session) {
        ModelAndView m = new ModelAndView("editSublecture");
        Sublecture editedSublecture = sublectureRepository.findById(id).orElse(new Sublecture());
        m.addObject("newSublecture", editedSublecture);
        m.addObject("lectureList", lectureService.findAll());
        session.setAttribute("editedSublecture", editedSublecture);
        return m;
    }

    @PostMapping(value = "/lecture/editSublecture")
    public String updateSublecture(@ModelAttribute("newSublecture") Sublecture sublecture, HttpSession session) {
        Sublecture editedSublecture = (Sublecture) session.getAttribute("editedSublecture");
        editedSublecture.setTitle(sublecture.getTitle());
        editedSublecture.setContent(sublecture.getContent());
        sublectureRepository.save(editedSublecture);
        return "redirect:/lecture";
    }

    @PostMapping(value = "/lecture/deleteSublecture")
    public String deleteSublecture(HttpSession session) {
        Sublecture editedSublecture = (Sublecture) session.getAttribute("editedSublecture");
        sublectureRepository.delete(editedSublecture);
        return "redirect:/lecture";
    }
}
