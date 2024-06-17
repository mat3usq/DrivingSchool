package com.driving.school.service;


import com.driving.school.model.Exam;
import com.driving.school.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;


    public Exam createExam(Exam exam) {
        return examRepository.save(exam);
    }


    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }


    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }


    public Exam updateExam(Long id, Exam examDetails) {
        Optional<Exam> optionalExam = examRepository.findById(id);
        if (optionalExam.isPresent()) {
            Exam exam = optionalExam.get();
            exam.setName(examDetails.getName());
            exam.setPoints(examDetails.getPoints());
            exam.setQuestionNumber(examDetails.getQuestionNumber());
            exam.setQuestions(examDetails.getQuestions());
            return examRepository.save(exam);
        } else {
            throw new RuntimeException("Exam not found with id " + id);
        }
    }


    public void deleteExam(Long id) {
        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
        } else {
            throw new RuntimeException("Exam not found with id " + id);
        }
    }
}
