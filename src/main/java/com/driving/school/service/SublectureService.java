package com.driving.school.service;

import com.driving.school.model.Sublecture;
import com.driving.school.repository.SubjectRepository;
import com.driving.school.repository.SublectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SublectureService {
    private final SublectureRepository sublectureRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public SublectureService(SublectureRepository sublectureRepository, SubjectRepository subjectRepository) {
        this.sublectureRepository = sublectureRepository;
        this.subjectRepository = subjectRepository;
    }

    public void save(Sublecture sublecture) {
        renumberSublecture(sublecture);
        sublecture.getSubjects().forEach(s -> {
            s.setSublecture(sublecture);
            s.setOrderIndex(subjectRepository.findMaxOrderIndex() + 1);
            subjectRepository.save(s);
        });
    }

    public void update(Sublecture sublecture) {
        renumberSublecture(sublecture);
        renumberAllSubLectures();
    }

    public Sublecture findById(Long id) {
        return sublectureRepository.findById(id).orElse(new Sublecture());
    }

    public void delete(Sublecture sublecture) {
        sublectureRepository.delete(sublecture);
        renumberAllSubLectures();
    }

    private void renumberSublecture(Sublecture sublecture) {
        List<Sublecture> sublectures = sublectureRepository.findAllByOrderByOrderIndex();

        if (sublecture.getOrderIndex() == -1){
            sublecture.setOrderIndex(sublectures.size() + 1);
        }
        else for (Sublecture sl : sublectures)
            if (sl.getOrderIndex() >= sublecture.getOrderIndex()) {
                sl.setOrderIndex(sl.getOrderIndex() + 1);
                sublectureRepository.save(sl);
            }

        sublectureRepository.save(sublecture);
    }

    public void renumberAllSubLectures() {
        List<Sublecture> sublectures = sublectureRepository.findAllByOrderByOrderIndex();
        for (int i = 0; i < sublectures.size(); i++) {
            Sublecture sublecture = sublectures.get(i);
            sublecture.setOrderIndex(i + 1);
            sublectureRepository.save(sublecture);
        }
    }
}
