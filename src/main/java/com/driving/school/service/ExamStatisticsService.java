package com.driving.school.service;

import com.driving.school.model.*;
import com.driving.school.repository.StudentExamStatisticsRepository;
import com.driving.school.repository.StudentTestStatisticsRepository;
import com.driving.school.repository.TestRepository;
import com.driving.school.repository.TestStatisticsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class ExamStatisticsService {
    private final StudentExamStatisticsRepository studentExamStatisticsRepository;

    @Autowired
    public ExamStatisticsService(StudentExamStatisticsRepository studentExamStatisticsRepository) {
        this.studentExamStatisticsRepository = studentExamStatisticsRepository;
    }

    public void updateStatisticsExamForUser(StudentExam studentExam) {
        StudentExamStatistics studentExamStatistics =
                studentExamStatisticsRepository.findBySchoolUserAndCategory(
                        studentExam.getSchoolUser(),
                        studentExam.getCategory()
                );

        if (studentExamStatistics == null) {
            studentExamStatistics = new StudentExamStatistics();
            studentExamStatistics.setSchoolUser(studentExam.getSchoolUser());
            studentExamStatistics.setCategory(studentExam.getCategory());
            studentExamStatistics.setAverageExamsDuration(studentExam.getExamDuration());
            studentExamStatistics.setAverageTimePerQuestions(studentExam.getAverageTimePerQuestion());
            studentExamStatistics.setAveragePoints(Double.valueOf(studentExam.getPoints()));
            studentExamStatistics.setNumberOfSolvedExams(1);
            studentExamStatistics.setNumberOfQuestionsAnsweredCorrectly(
                    studentExam.getAmountCorrectSpecAnswers() +
                            studentExam.getAmountCorrectNoSpecAnswers()
            );
            studentExamStatistics.setNumberOfQuestionsSkipped(studentExam.getAmountSkippedQuestions());
            studentExamStatistics.setNumberOfQuestionsAnsweredInCorrectly(
                    32 - studentExamStatistics.getNumberOfQuestionsAnsweredCorrectly()
            );

            if (studentExam.getPassed())
                studentExamStatistics.setNumberOfPassedExams(1);
            else
                studentExamStatistics.setNumberOfPassedExams(0);
        } else {
            int updatedNumberOfSolvedExams = studentExamStatistics.getNumberOfSolvedExams() + 1;
            studentExamStatistics.setNumberOfSolvedExams(updatedNumberOfSolvedExams);

            double currentAverageDuration = studentExamStatistics.getAverageExamsDuration().getSeconds();
            double newDuration = studentExam.getExamDuration().getSeconds();
            double updatedAverageDuration =
                    ((currentAverageDuration * (updatedNumberOfSolvedExams - 1)) + newDuration)
                            / updatedNumberOfSolvedExams;
            studentExamStatistics.setAverageExamsDuration(Duration.ofSeconds((long) updatedAverageDuration));

            double currentAverageTimePerQuestion = studentExamStatistics.getAverageTimePerQuestions();
            double newTimePerQuestion = studentExam.getAverageTimePerQuestion();
            double updatedAverageTimePerQuestion =
                    ((currentAverageTimePerQuestion * (updatedNumberOfSolvedExams - 1)) + newTimePerQuestion)
                            / updatedNumberOfSolvedExams;
            studentExamStatistics.setAverageTimePerQuestions(updatedAverageTimePerQuestion);

            double currentAveragePoints = studentExamStatistics.getAveragePoints();
            double newPoints = studentExam.getPoints();
            double updatedAveragePoints =
                    ((currentAveragePoints * (updatedNumberOfSolvedExams - 1)) + newPoints)
                            / updatedNumberOfSolvedExams;
            studentExamStatistics.setAveragePoints(updatedAveragePoints);

            int newCorrectAnswers =
                    studentExam.getAmountCorrectSpecAnswers() +
                            studentExam.getAmountCorrectNoSpecAnswers();
            int updatedCorrectAnswers =
                    studentExamStatistics.getNumberOfQuestionsAnsweredCorrectly() + newCorrectAnswers;
            studentExamStatistics.setNumberOfQuestionsAnsweredCorrectly(updatedCorrectAnswers);

            int updatedSkippedQuestions =
                    studentExamStatistics.getNumberOfQuestionsSkipped() +
                            studentExam.getAmountSkippedQuestions();
            studentExamStatistics.setNumberOfQuestionsSkipped(updatedSkippedQuestions);

            int updatedIncorrectAnswers = studentExamStatistics.getNumberOfQuestionsAnsweredInCorrectly() +
                    (32 - (studentExam.getAmountCorrectNoSpecAnswers() + studentExam.getAmountCorrectSpecAnswers()));
            studentExamStatistics.setNumberOfQuestionsAnsweredInCorrectly(updatedIncorrectAnswers);

            if (studentExam.getPassed()) {
                int updatedPassedExams = studentExamStatistics.getNumberOfPassedExams() + 1;
                studentExamStatistics.setNumberOfPassedExams(updatedPassedExams);
            }
        }

        studentExamStatisticsRepository.save(studentExamStatistics);
    }
}
