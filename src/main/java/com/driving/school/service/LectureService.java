package com.driving.school.service;

import com.driving.school.model.*;
import com.driving.school.repository.LectureRepository;
import com.driving.school.repository.SubjectRepository;
import com.driving.school.repository.SublectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final SublectureRepository sublectureRepository;
    private final SubjectRepository subjectRepository;
    private final NotificationService notificationService;

    @Autowired
    public LectureService(LectureRepository lectureRepository, SublectureRepository sublectureRepository, SubjectRepository subjectRepository, NotificationService notificationService) {
        this.lectureRepository = lectureRepository;
        this.sublectureRepository = sublectureRepository;
        this.subjectRepository = subjectRepository;
        this.notificationService = notificationService;
    }

    public List<Lecture> findAllByCategory(String category) {
        List<Lecture> lectures = lectureRepository.findAllByCategoryOrderByOrderIndex(category);
        for (Lecture lecture : lectures) {
            lecture.getSublectures().sort(Comparator.comparingInt(Sublecture::getOrderIndex));
            for (Sublecture sublecture : lecture.getSublectures())
                sublecture.getSubjects().sort(Comparator.comparingInt(Subject::getOrderIndex));
        }
        return lectures;
    }

    public void save(Lecture lecture, SchoolUser user) {
        String category = user.getCurrentCategory();
        lecture.setCategory(category);
        renumberLecture(lecture);
        lecture.getSublectures().forEach(sl -> {
            sl.setLecture(lecture);
            sl.setOrderIndex(sublectureRepository.findMaxOrderIndex() + 1);
            sublectureRepository.save(sl);

            sl.getSubjects().forEach(s -> {
                s.setSublecture(sl);
                s.setOrderIndex(subjectRepository.findMaxOrderIndex() + 1);
                subjectRepository.save(s);
            });
        });

        if (user.getRoleName().equals(Constants.INSTRUCTOR_ROLE))
            notificationService.sendNotificationWhenInstructorCreateNewLecture(user);
    }

    public void update(Lecture lecture) {
        renumberLecture(lecture);
        renumberAllLectures(lecture);
    }

    public Lecture findById(Long id) {
        return lectureRepository.findById(id).orElse(new Lecture());
    }

    public void delete(Lecture lecture) {
        lectureRepository.delete(lecture);
        renumberAllLectures(lecture);
    }

    private void renumberLecture(Lecture lecture) {
        List<Lecture> lectures = lectureRepository.findAllByCategoryOrderByOrderIndex(lecture.getCategory());

        if (lecture.getOrderIndex() == -1)
            lecture.setOrderIndex(lectures.size() + 1);
        else for (Lecture l : lectures)
            if (l.getOrderIndex() >= lecture.getOrderIndex()) {
                l.setOrderIndex(l.getOrderIndex() + 1);
                lectureRepository.save(l);
            }

        lectureRepository.save(lecture);
    }

    public void renumberAllLectures(Lecture l) {
        List<Lecture> lectures = lectureRepository.findAllByCategoryOrderByOrderIndex(l.getCategory());
        for (int i = 0; i < lectures.size(); i++) {
            Lecture lecture = lectures.get(i);
            lecture.setOrderIndex(i + 1);
            lectureRepository.save(lecture);
        }
    }
}
