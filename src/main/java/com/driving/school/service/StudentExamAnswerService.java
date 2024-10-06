package com.driving.school.service;


import com.driving.school.model.StudentExamAnswer;
import com.driving.school.repository.StudentExamAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentExamAnswerService {

    @Autowired
    private StudentExamAnswerRepository studentExamAnswerRepository;


    public StudentExamAnswer save(StudentExamAnswer studentExamAnswer) {
        return studentExamAnswerRepository.save(studentExamAnswer);
    }


    public Optional<StudentExamAnswer> findById(long id) {
        return studentExamAnswerRepository.findById(id);
    }


    public List<StudentExamAnswer> findAll() {
        return studentExamAnswerRepository.findAll();
    }


    public void deleteById(long id) {
        studentExamAnswerRepository.deleteById(id);
    }

}
