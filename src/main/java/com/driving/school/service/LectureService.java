package com.driving.school.service;

import com.driving.school.model.Lecture;
import com.driving.school.model.Sublecture;
import com.driving.school.repository.LectureRepository;
import com.driving.school.repository.SubjectRepository;
import com.driving.school.repository.SublectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final SublectureRepository sublectureRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public LectureService(LectureRepository lectureRepository, SublectureRepository sublectureRepository, SubjectRepository subjectRepository) {
        this.lectureRepository = lectureRepository;
        this.sublectureRepository = sublectureRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Lecture> findAll() {
        return lectureRepository.findAll();
    }

    public void save(Lecture lecture) {
        lectureRepository.save(lecture);
        lecture.getSublectures().forEach(sl ->{
            sl.setLecture(lecture);
            sublectureRepository.save(sl);

            sl.getSubjects().forEach(s -> {
                s.setSublecture(sl);
                subjectRepository.save(s);
            });
        });
    }
}