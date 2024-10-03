package com.driving.school.service;


import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentExam;
import com.driving.school.repository.StudentExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentExamService {
    private final StudentExamRepository studentExamRepository;
    private final QuestionService questionService;

    @Autowired
    public StudentExamService(StudentExamRepository studentExamRepository, QuestionService questionService) {
        this.studentExamRepository = studentExamRepository;
        this.questionService = questionService;
    }


    public StudentExam createStudentExam(StudentExam studentExam) {
        return studentExamRepository.save(studentExam);
    }


    public StudentExam getStudentExamById(Long id) {
        return studentExamRepository.findById(id).orElse(null);
    }


    public List<StudentExam> getAllStudentExams() {
        return studentExamRepository.findAll();
    }


    public StudentExam updateStudentExam(Long id, StudentExam studentExamDetails) {
        Optional<StudentExam> optionalStudentExam = studentExamRepository.findById(id);
        if (optionalStudentExam.isPresent()) {
            StudentExam studentExam = optionalStudentExam.get();
            studentExam.setCategory(studentExamDetails.getCategory());
            studentExam.setPoints(studentExamDetails.getPoints());
            studentExam.setSchoolUser(studentExamDetails.getSchoolUser());
            studentExam.setStudentExamAnswers(studentExamDetails.getStudentExamAnswers());
            return studentExamRepository.save(studentExam);
        } else {
            throw new RuntimeException("StudentExam not found with id " + id);
        }
    }

    // Delete a StudentExam by ID
    public void deleteStudentExam(Long id) {
        if (studentExamRepository.existsById(id)) {
            studentExamRepository.deleteById(id);
        } else {
            throw new RuntimeException("StudentExam not found with id " + id);
        }
    }

    public List<StudentExam> getStudentExamsBySchoolUser(SchoolUser schoolUser) {
        return studentExamRepository.findBySchoolUser(schoolUser);
    }

    public List<Question> generateQuestionSet(String category) {
        int numberOfNoSpecialistQuestions = 20;
        int numberOfSpecialistQuestions = 12;
        List<Question> noSpecialistQuestions = questionService.getRandomNoSpecialistcQuestionsByCategory(category, numberOfNoSpecialistQuestions);
        List<Question> specialistQuestions = questionService.getRandomSpecialistcQuestionsByCategory(category, numberOfSpecialistQuestions);

        noSpecialistQuestions.addAll(specialistQuestions);
        return noSpecialistQuestions;
    }
}
