package com.driving.school.controller;

import com.driving.school.model.Lecture;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.Subject;
import com.driving.school.model.Sublecture;
import com.driving.school.service.LectureService;
import com.driving.school.service.SubjectService;
import com.driving.school.service.SublectureService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class LectureController {
    private final LectureService lectureService;
    private final SublectureService sublectureService;
    private final SubjectService subjectService;

    @Autowired
    public LectureController(LectureService lectureService, SublectureService sublectureService, SubjectService subjectService) {
        this.lectureService = lectureService;
        this.sublectureService = sublectureService;
        this.subjectService = subjectService;
    }

    @GetMapping(value = "/lecture")
    public ModelAndView displayLecturePage(HttpSession session, RedirectAttributes redirectAttributes) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");

        if (user.getCurrentCategory().isEmpty()) {
            redirectAttributes.addFlashAttribute("notChoosenCategoryInfo", "Proszę wybierz kategorię, aby pomyślnie zacząć naukę teorii!");
            return new ModelAndView("redirect:/dashboard");
        }

        ModelAndView m = new ModelAndView("lecture");
        m.addObject("newLecture", new Lecture());
        m.addObject("newSublecture", new Sublecture());
        m.addObject("newSubject", new Subject());
        m.addObject("lectureList", lectureService.findAllByCategory(((SchoolUser) session.getAttribute("loggedInUser")).getCurrentCategory()));
        return m;
    }

    @PostMapping(value = "/lecture/addLecture")
    public String addLecture(@ModelAttribute("newLecture") Lecture lecture, HttpSession session) {
        lecture.getSublectures().forEach(sublecture -> {
            sublecture.getSubjects().forEach(subject -> {
                if (subject != null && subject.getFile() != null && !subject.getFile().isEmpty()) {
                    try {
                        subject.setImage(subject.getFile().getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        lectureService.save(lecture, (SchoolUser) session.getAttribute("loggedInUser"));
        return "redirect:/lecture";
    }

    @GetMapping(value = "/lecture/editLecture/{id}")
    public ModelAndView redirectToEditLecture(@PathVariable("id") Long id, HttpSession session) {
        ModelAndView m = new ModelAndView("editLecture");
        Lecture editedLecture = lectureService.findById(id);
        m.addObject("newLecture", editedLecture);
        m.addObject("lectureList", lectureService.findAllByCategory(((SchoolUser) session.getAttribute("loggedInUser")).getCurrentCategory()));
        session.setAttribute("editedLecture", editedLecture);
        return m;
    }

    @PostMapping(value = "/lecture/editLecture")
    public String updateLecture(@ModelAttribute("newLecture") Lecture lecture, HttpSession session) {
        Lecture editedLecture = (Lecture) session.getAttribute("editedLecture");
        editedLecture.setName(lecture.getName());
        editedLecture.setContent(lecture.getContent());
        editedLecture.setOrderIndex(lecture.getOrderIndex());
        lectureService.update(editedLecture);
        return "redirect:/lecture";
    }

    @PostMapping(value = "/lecture/deleteLecture")
    public String deleteLecture(HttpSession session) {
        Lecture editedLecture = (Lecture) session.getAttribute("editedLecture");
        lectureService.delete(editedLecture);
        return "redirect:/lecture";
    }

    @PostMapping(value = "/lecture/addSublecture")
    public String addSublecture(@ModelAttribute("newSublecture") Sublecture sublecture) {
        sublecture.getSubjects().forEach(subject -> {
            if (subject != null && subject.getFile() != null && !subject.getFile().isEmpty()) {
                try {
                    subject.setImage(subject.getFile().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        sublectureService.save(sublecture);
        return "redirect:/lecture";
    }

    @GetMapping(value = "/lecture/editSublecture/{id}")
    public ModelAndView redirectToEditSublecture(@PathVariable("id") Long id, HttpSession session) {
        ModelAndView m = new ModelAndView("editSublecture");
        Sublecture editedSublecture = sublectureService.findById(id);
        m.addObject("newSublecture", editedSublecture);
        m.addObject("lectureList", lectureService.findAllByCategory(((SchoolUser) session.getAttribute("loggedInUser")).getCurrentCategory()));
        session.setAttribute("editedSublecture", editedSublecture);
        return m;
    }

    @PostMapping(value = "/lecture/editSublecture")
    public String updateSublecture(@ModelAttribute("newSublecture") Sublecture sublecture, HttpSession session) {
        Sublecture editedSublecture = (Sublecture) session.getAttribute("editedSublecture");
        editedSublecture.setTitle(sublecture.getTitle());
        editedSublecture.setContent(sublecture.getContent());
        editedSublecture.setLecture(sublecture.getLecture());
        editedSublecture.setOrderIndex(sublecture.getOrderIndex());
        sublectureService.update(editedSublecture);
        return "redirect:/lecture";
    }

    @PostMapping(value = "/lecture/deleteSublecture")
    public String deleteSublecture(HttpSession session) {
        Sublecture editedSublecture = (Sublecture) session.getAttribute("editedSublecture");
        sublectureService.delete(editedSublecture);
        return "redirect:/lecture";
    }

    @PostMapping(value = "/lecture/addSubject")
    public String addSubject(@ModelAttribute("newSubject") Subject subject) {
        System.out.println(subject);
        if (subject != null && subject.getFile() != null && !subject.getFile().isEmpty()) {
            try {
                subject.setImage(subject.getFile().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        subjectService.save(subject);
        return "redirect:/lecture";
    }

    @GetMapping(value = "/lecture/editSubject/{id}")
    public ModelAndView redirectToEditSubject(@PathVariable("id") Long id, HttpSession session) {
        ModelAndView m = new ModelAndView("editSubject");
        Subject editedSubject = subjectService.findById(id);
        m.addObject("newSubject", editedSubject);
        m.addObject("lectureList", lectureService.findAllByCategory(((SchoolUser) session.getAttribute("loggedInUser")).getCurrentCategory()));
        session.setAttribute("editedSubject", editedSubject);
        return m;
    }

    @PostMapping(value = "/lecture/editSubject")
    public String updateSubject(@ModelAttribute("newSubject") Subject subject, HttpSession session) {
        Subject editedSubject = (Subject) session.getAttribute("editedSubject");
        editedSubject.setTitle(subject.getTitle());
        editedSubject.setContent(subject.getContent());
        editedSubject.setSublecture(subject.getSublecture());
        editedSubject.setOrderIndex(subject.getOrderIndex());
        if (!subject.getFile().isEmpty()) {
            try {
                editedSubject.setImage(subject.getFile().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        subjectService.update(editedSubject);
        return "redirect:/lecture";
    }

    @PostMapping(value = "/lecture/deleteSubject")
    public String deleteSubject(HttpSession session) {
        Subject editedSubject = (Subject) session.getAttribute("editedSubject");
        subjectService.delete(editedSubject);
        return "redirect:/lecture";
    }
}
