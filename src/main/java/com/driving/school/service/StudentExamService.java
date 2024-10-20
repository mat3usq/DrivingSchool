package com.driving.school.service;


import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentExam;
import com.driving.school.repository.StudentExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class StudentExamService {
    private final StudentExamRepository studentExamRepository;
    private final QuestionService questionService;
    private final ExamStatisticsService examStatisticsService;

    @Autowired
    public StudentExamService(StudentExamRepository studentExamRepository, QuestionService questionService, ExamStatisticsService examStatisticsService) {
        this.studentExamRepository = studentExamRepository;
        this.questionService = questionService;
        this.examStatisticsService = examStatisticsService;
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
        int numberOfNoSpecialistQuestionsWithOnePoint = 4;
        int numberOfNoSpecialistQuestionsWithTwoPoints = 6;
        int numberOfNoSpecialistQuestionsWithThreePoints = 10;
        int numberOfSpecialistQuestionsWithOnePoint = 2;
        int numberOfSpecialistQuestionsWithTwoPoints = 4;
        int numberOfSpecialistQuestionsWithThreePoints = 6;

        List<Question> noSpecialistQuestions = new ArrayList<>();
        noSpecialistQuestions.addAll(questionService.getRandomNoSpecialistcQuestionsByCategory(category, 1, numberOfNoSpecialistQuestionsWithOnePoint));
        noSpecialistQuestions.addAll(questionService.getRandomNoSpecialistcQuestionsByCategory(category, 2, numberOfNoSpecialistQuestionsWithTwoPoints));
        noSpecialistQuestions.addAll(questionService.getRandomNoSpecialistcQuestionsByCategory(category, 3, numberOfNoSpecialistQuestionsWithThreePoints));
        Collections.shuffle(noSpecialistQuestions);

        List<Question> specialistQuestions = new ArrayList<>();
        specialistQuestions.addAll(questionService.getRandomSpecialistcQuestionsByCategory(category, 1, numberOfSpecialistQuestionsWithOnePoint));
        specialistQuestions.addAll(questionService.getRandomSpecialistcQuestionsByCategory(category, 2, numberOfSpecialistQuestionsWithTwoPoints));
        specialistQuestions.addAll(questionService.getRandomSpecialistcQuestionsByCategory(category, 3, numberOfSpecialistQuestionsWithThreePoints));
        Collections.shuffle(specialistQuestions);

        noSpecialistQuestions.addAll(specialistQuestions);
        return noSpecialistQuestions;
    }

    public StudentExam setSummaryOfExam(StudentExam studentExam) {
        studentExam.setEndTime(LocalDateTime.now());
        studentExam.setExamDuration(Duration.between(studentExam.getStartTime(), studentExam.getEndTime()));
        studentExam.setAverageTimePerQuestion((studentExam.getExamDuration().toSeconds() / 32.0));
        studentExam.setAmountCorrectNoSpecAnswers((int) studentExam.getStudentExamAnswers().stream()
                .filter(a -> !a.getQuestion().getQuestionType() && a.getCorrectness())
                .count());
        studentExam.setAmountCorrectSpecAnswers((int) studentExam.getStudentExamAnswers().stream()
                .filter(a -> a.getQuestion().getQuestionType() && a.getCorrectness())
                .count());
        studentExam.setAmountSkippedQuestions((int) studentExam.getStudentExamAnswers().stream()
                .filter(a -> a.getAnswer().isEmpty() && !a.getCorrectness())
                .count());
        studentExam.setPassed(studentExam.getPoints() >= 68);
        studentExam.setExamDurationString(String.format("%dm %ds", studentExam.getExamDuration().toMinutes(), studentExam.getExamDuration().minusMinutes(studentExam.getExamDuration().toMinutes()).getSeconds()));

        updateStudentExam(studentExam.getId(), studentExam);
        examStatisticsService.updateStatisticsExamForUser(studentExam);

        return studentExam;
    }
}
