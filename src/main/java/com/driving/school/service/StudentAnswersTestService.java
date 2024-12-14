package com.driving.school.service;


import com.driving.school.model.*;
import com.driving.school.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentAnswersTestService {
    private final StudentAnswersTestRepository studentAnswersTestRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final SchoolUserService schoolUserService;
    private final TestStatisticsService testStatisticsService;

    @Autowired
    public StudentAnswersTestService(StudentAnswersTestRepository studentAnswersTestRepository, TestRepository testRepository, QuestionRepository questionRepository, SchoolUserService schoolUserService, TestStatisticsService testStatisticsService) {
        this.studentAnswersTestRepository = studentAnswersTestRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.schoolUserService = schoolUserService;
        this.testStatisticsService = testStatisticsService;
    }

    public void deleteAllStudentAnswersTest(List<StudentAnswersTest> studentAnswersTestList) {
        studentAnswersTestRepository.deleteAll(studentAnswersTestList);
    }

    public StudentAnswersTest save(SchoolUser loggedUser, Long testId, Long questionId, String answer,  LocalDateTime timeStartAnswer) {
        Question q = questionRepository.findById(questionId).orElse(null);
        Test t = testRepository.findById(testId).orElse(null);
        StudentAnswersTest studentAnswersTest = new StudentAnswersTest();

        switch (loggedUser.getSelectedTypeQuestions()) {
            case "correctAnswers":
            case "incorrectAnswers":
            case "skippedQuestions":
            case "likedQuestions":
                studentAnswersTest = studentAnswersTestRepository.findBySchoolUserAndTestAndQuestion(loggedUser, t, q);

                if (studentAnswersTest == null) {
                    studentAnswersTest = new StudentAnswersTest();
                    studentAnswersTest.setSchoolUser(loggedUser);
                    studentAnswersTest.setTest(t);
                    studentAnswersTest.setQuestion(q);
                }

                if (q != null && !answer.equals("SKIP")) {
                    studentAnswersTest.setCorrectness(q.getCorrectAnswer().equals(answer));
                    studentAnswersTest.setSkipped(false);
                } else {
                    studentAnswersTest.setCorrectness(false);
                    studentAnswersTest.setSkipped(true);
                }
                saveToDb(studentAnswersTest, timeStartAnswer);
                break;
            case "remainingQuestions":
                studentAnswersTest.setSchoolUser(loggedUser);
                studentAnswersTest.setTest(t);
                studentAnswersTest.setQuestion(q);

                if (q != null && !answer.equals("SKIP")) {
                    studentAnswersTest.setCorrectness(q.getCorrectAnswer().equals(answer));
                    studentAnswersTest.setSkipped(false);
                } else {
                    studentAnswersTest.setCorrectness(false);
                    studentAnswersTest.setSkipped(true);
                }
                saveToDb(studentAnswersTest, timeStartAnswer);
                break;
        }

        return studentAnswersTest;
    }

    public void saveToDb(StudentAnswersTest studentAnswersTest, LocalDateTime timeStartAnswer) {
        studentAnswersTest.setDurationOfAnswer(Duration.between(timeStartAnswer, LocalDateTime.now()).getSeconds());
        studentAnswersTestRepository.delete(studentAnswersTest);
        studentAnswersTestRepository.save(studentAnswersTest);
        testStatisticsService.updateStatisticsAnswersForUser(studentAnswersTest);
    }

    public void setStatisticForTest(List<Test> tests, Long userId) {
        tests.forEach(test -> {
            if (test.getNumberQuestions() != 0) {
                double answers = (double) studentAnswersTestRepository.countAnswersByUserIdAndTestId(userId, test.getId());
                double questions = (double) test.getNumberQuestions();
                double result = answers / questions * 100;
                test.setCounter((int) result);
            } else test.setCounter(0);
        });
    }

    public List<StudentAnswersTest> getCorrectStudentAnswersTestByUserIdandTestId(Long userId, Long testId) {
        List<StudentAnswersTest> list = studentAnswersTestRepository.findAllBySchoolUserAndTest(schoolUserService.findUserById(userId), testRepository.findById(testId).orElse(null));
        return list.stream().filter(l -> l.getCorrectness() && !l.getSkipped()).toList();
    }

    public List<StudentAnswersTest> getInCorrectStudentAnswersTestByUserIdandTestId(Long userId, Long testId) {
        List<StudentAnswersTest> list = studentAnswersTestRepository.findAllBySchoolUserAndTest(schoolUserService.findUserById(userId), testRepository.findById(testId).orElse(null));
        return list.stream().filter(l -> !l.getCorrectness() && !l.getSkipped()).toList();
    }

    public List<StudentAnswersTest> getSkippedStudentAnswersTestByUserIdandTestId(Long userId, Long testId) {
        List<StudentAnswersTest> list = studentAnswersTestRepository.findAllBySchoolUserAndTest(schoolUserService.findUserById(userId), testRepository.findById(testId).orElse(null));
        return list.stream().filter(StudentAnswersTest::getSkipped).toList();
    }

    public List<Question> findQuestionsByUserIdAndTestId(Long userId, Long testId) {
        return studentAnswersTestRepository.findQuestionsByUserIdAndTestId(userId, testId);
    }

    public List<StudentAnswersTest> findAllBySchoolUserAndTest(SchoolUser user, Test test) {
        return studentAnswersTestRepository.findAllBySchoolUserAndTest(user, test);
    }
}
