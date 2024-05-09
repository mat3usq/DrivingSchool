package com.driving.school.service;

import com.driving.school.model.Lecture;
import com.driving.school.model.Sublecture;
import com.driving.school.repository.LectureRepository;
import com.driving.school.repository.SublectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final SublectureRepository sublectureRepository;

    @Autowired
    public LectureService(LectureRepository lectureRepository, SublectureRepository sublectureRepository) {
        this.lectureRepository = lectureRepository;
        this.sublectureRepository = sublectureRepository;
    }

    public List<Lecture> findAll() {
        return lectureRepository.findAll();
    }

    public void save(Lecture lecture) {
        lectureRepository.save(lecture);
        for (Sublecture sublecture : lecture.getSublectures()) {
            sublecture.setLecture(lecture);
            sublectureRepository.save(sublecture);
        }
    }
}
