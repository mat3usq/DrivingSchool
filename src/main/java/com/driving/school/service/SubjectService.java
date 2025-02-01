package com.driving.school.service;

import com.driving.school.model.Subject;
import com.driving.school.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public void save(Subject subject) {
        renumberSubject(subject);
        subjectRepository.save(subject);
    }

    public void update(Subject subject) {
        renumberSubject(subject);
        renumberAllSubject();
    }

    public Subject findById(Long id) {
        return subjectRepository.findById(id).orElse(new Subject());
    }

    public void delete(Subject subject) {
        subjectRepository.delete(subject);
        renumberAllSubject();
    }

    private void renumberSubject(Subject subject) {
        List<Subject> subjects = subjectRepository.findAllByOrderByOrderIndex();

        if (subject.getOrderIndex() == -1) {
            subject.setOrderIndex(subjects.size() + 1);
        } else for (Subject s : subjects)
            if (s.getOrderIndex() >= subject.getOrderIndex()) {
                s.setOrderIndex(s.getOrderIndex() + 1);
                subjectRepository.save(s);
            }

        subjectRepository.save(subject);
    }

    public void renumberAllSubject() {
        List<Subject> subjects = subjectRepository.findAllByOrderByOrderIndex();
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            subject.setOrderIndex(i + 1);
            subjectRepository.save(subject);
        }
    }
}
