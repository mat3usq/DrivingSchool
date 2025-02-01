package com.driving.school.service;

import com.driving.school.model.*;
import com.driving.school.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExamStatisticsService {
    private final StudentExamStatisticsRepository studentExamStatisticsRepository;
    private final ExamStatisticsRepository examStatisticsRepository;

    @Autowired
    public ExamStatisticsService(StudentExamStatisticsRepository studentExamStatisticsRepository, ExamStatisticsRepository examStatisticsRepository) {
        this.studentExamStatisticsRepository = studentExamStatisticsRepository;
        this.examStatisticsRepository = examStatisticsRepository;
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
            studentExamStatistics.setAverageExamsDuration((double) studentExam.getExamDuration().getSeconds());
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

            double currentAverageDuration = studentExamStatistics.getAverageExamsDuration();
            double newDuration = studentExam.getExamDuration().getSeconds();
            double updatedAverageDuration =
                    ((currentAverageDuration * (updatedNumberOfSolvedExams - 1)) + newDuration)
                            / updatedNumberOfSolvedExams;
            studentExamStatistics.setAverageExamsDuration(updatedAverageDuration);

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


    @Transactional
    public void updateAllExamStatistics() {
        List<Object[]> aggregatedStats = studentExamStatisticsRepository.aggregateExamStatistics();

        for (Object[] row : aggregatedStats) {
            String category = (String) row[0];
            Double averagePoints = (Double) row[1];
            Long numberOfSolvedExams = (Long) row[2];
            Long numberOfPassedExams = (Long) row[3];
            Double averageExamsDuration = (Double) row[4];
            Double averageTimePerQuestions = (Double) row[5];
            Long numberOfQuestionsAnsweredCorrectly = (Long) row[6];
            Long numberOfQuestionsAnsweredInCorrectly = (Long) row[7];
            Long numberOfQuestionsSkipped = (Long) row[8];

            Optional<ExamStatistics> optionalStatistics = examStatisticsRepository.findByCategory(category);
            ExamStatistics examStatistics = optionalStatistics.orElseGet(ExamStatistics::new);

            examStatistics.setCategory(category);
            examStatistics.setAveragePoints(averagePoints);
            examStatistics.setNumberOfSolvedExams(numberOfSolvedExams.intValue());
            examStatistics.setNumberOfPassedExams(numberOfPassedExams.intValue());
            examStatistics.setAverageExamsDuration(averageExamsDuration);
            examStatistics.setAverageTimePerQuestions(averageTimePerQuestions);
            examStatistics.setNumberOfQuestionsAnsweredCorrectly(numberOfQuestionsAnsweredCorrectly.intValue());
            examStatistics.setNumberOfQuestionsAnsweredInCorrectly(numberOfQuestionsAnsweredInCorrectly.intValue());
            examStatistics.setNumberOfQuestionsSkipped(numberOfQuestionsSkipped.intValue());
            examStatisticsRepository.save(examStatistics);
        }
    }
}
