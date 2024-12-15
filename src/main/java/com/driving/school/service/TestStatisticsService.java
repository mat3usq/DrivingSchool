package com.driving.school.service;

import com.driving.school.model.StudentAnswersTest;
import com.driving.school.model.StudentTestStatistics;
import com.driving.school.model.Test;
import com.driving.school.model.TestStatistics;
import com.driving.school.repository.StudentTestStatisticsRepository;
import com.driving.school.repository.TestRepository;
import com.driving.school.repository.TestStatisticsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TestStatisticsService {
    private final StudentTestStatisticsRepository studentTestStatisticsRepository;
    private final TestStatisticsRepository testStatisticsRepository;
    private final TestRepository testRepository;

    @Autowired
    public TestStatisticsService(StudentTestStatisticsRepository studentTestStatisticsRepository, TestStatisticsRepository testStatisticsRepository, TestRepository testRepository) {
        this.studentTestStatisticsRepository = studentTestStatisticsRepository;
        this.testStatisticsRepository = testStatisticsRepository;
        this.testRepository = testRepository;
    }

    public void updateStatisticsAnswersForUser(StudentAnswersTest studentAnswersTest) {
        StudentTestStatistics studentTestStatistics = studentTestStatisticsRepository.findBySchoolUserAndTest(studentAnswersTest.getSchoolUser(), studentAnswersTest.getTest());

        if (studentTestStatistics == null) {
            studentTestStatistics = new StudentTestStatistics();
            studentTestStatistics.setSchoolUser(studentAnswersTest.getSchoolUser());
            studentTestStatistics.setTest(studentAnswersTest.getTest());
            studentTestStatistics.setAverageDurationOfAnswers((double) studentAnswersTest.getDurationOfAnswer());
            studentTestStatistics.setNumberOfQuestionsSolved(1);

            if (studentAnswersTest.getSkipped()) {
                studentTestStatistics.setNumberOfQuestionsSkipped(1);
            } else if (studentAnswersTest.getCorrectness()) {
                studentTestStatistics.setNumberOfQuestionsAnsweredCorrectly(1);
            } else {
                studentTestStatistics.setNumberOfQuestionsAnsweredInCorrectly(1);
            }
        } else {
            double currentAverage = studentTestStatistics.getAverageDurationOfAnswers();
            int currentCount = studentTestStatistics.getNumberOfQuestionsSolved();
            double newAverage = ((currentAverage * currentCount) + studentAnswersTest.getDurationOfAnswer()) / (currentCount + 1);
            studentTestStatistics.setAverageDurationOfAnswers(newAverage);

            studentTestStatistics.setNumberOfQuestionsSolved(currentCount + 1);

            if (studentAnswersTest.getSkipped()) {
                studentTestStatistics.setNumberOfQuestionsSkipped(
                        studentTestStatistics.getNumberOfQuestionsSkipped() + 1
                );
            } else if (studentAnswersTest.getCorrectness()) {
                studentTestStatistics.setNumberOfQuestionsAnsweredCorrectly(
                        studentTestStatistics.getNumberOfQuestionsAnsweredCorrectly() + 1
                );
            } else {
                studentTestStatistics.setNumberOfQuestionsAnsweredInCorrectly(
                        studentTestStatistics.getNumberOfQuestionsAnsweredInCorrectly() + 1
                );
            }
        }

        studentTestStatisticsRepository.save(studentTestStatistics);
    }

    @Transactional
    public void updateAllTestStatistics() {
        List<Object[]> aggregatedStats = studentTestStatisticsRepository.aggregateTestStatistics();

        for (Object[] stat : aggregatedStats) {
            Long testId = (Long) stat[0];
            Long numberOfQuestionsSolved = (Long) stat[1];
            Long numberOfQuestionsAnsweredCorrectly = (Long) stat[2];
            Long numberOfQuestionsAnsweredInCorrectly = (Long) stat[3];
            Long numberOfQuestionsSkipped = (Long) stat[4];
            Double averageDurationOfAnswers = (Double) stat[5];

            Optional<TestStatistics> optionalStatistics = testStatisticsRepository.findByTestId(testId);

            TestStatistics statistics;
            if (optionalStatistics.isPresent()) {
                statistics = optionalStatistics.get();
            } else {
                statistics = new TestStatistics();
                Test test = testRepository.findById(testId)
                        .orElseThrow(() -> new RuntimeException("Test o ID " + testId + " nie istnieje."));
                statistics.setTest(test);
            }

            statistics.setNumberOfQuestionsSolved(numberOfQuestionsSolved.intValue());
            statistics.setNumberOfQuestionsAnsweredCorrectly(numberOfQuestionsAnsweredCorrectly.intValue());
            statistics.setNumberOfQuestionsAnsweredInCorrectly(numberOfQuestionsAnsweredInCorrectly.intValue());
            statistics.setNumberOfQuestionsSkipped(numberOfQuestionsSkipped.intValue());
            statistics.setAverageDurationOfAnswers(averageDurationOfAnswers);

            testStatisticsRepository.save(statistics);
        }
    }
}
