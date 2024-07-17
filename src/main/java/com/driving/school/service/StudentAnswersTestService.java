package com.driving.school.service;


import com.driving.school.model.*;
import com.driving.school.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentAnswersTestService {
    private final StudentAnswersTestRepository studentAnswersTestRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final SchoolUserRepository schoolUserRepository;
    private final UserLikedQuestionRepository userLikedQuestionRepository;

    @Autowired
    public StudentAnswersTestService(StudentAnswersTestRepository studentAnswersTestRepository, TestRepository testRepository, QuestionRepository questionRepository, SchoolUserRepository schoolUserRepository, UserLikedQuestionRepository userLikedQuestionRepository) {
        this.studentAnswersTestRepository = studentAnswersTestRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.schoolUserRepository = schoolUserRepository;
        this.userLikedQuestionRepository = userLikedQuestionRepository;
    }

    public StudentAnswersTest saveTest(StudentAnswersTest test) {
        return studentAnswersTestRepository.save(test);
    }

    public StudentAnswersTest getTestById(Long id) {
        return studentAnswersTestRepository.findById(id).orElse(null);
    }

    public List<StudentAnswersTest> getAllTests() {
        return studentAnswersTestRepository.findAll();
    }

    public void deleteTestById(Long id) {
        studentAnswersTestRepository.deleteById(id);
    }

    public StudentAnswersTest save(SchoolUser loggedUser, Long testId, Long questionId, String answer, Boolean isLiked) {
        Question q = questionRepository.findById(questionId).orElse(null);
        Test t = testRepository.findById(testId).orElse(null);
        StudentAnswersTest studentAnswersTest = new StudentAnswersTest();

        switch (loggedUser.getSelectedTypeQuestions()) {
            case "correctAnswers":
            case "incorrectAnswers":
            case "skippedQuestions":
                studentAnswersTest = studentAnswersTestRepository.findBySchoolUserAndTestAndQuestion(loggedUser, t, q);
                if (q != null && !answer.equals("SKIP")) {
                    studentAnswersTest.setCorrectness(q.getCorrectAnswer().equals(answer));
                    studentAnswersTest.setSkipped(false);
                } else {
                    studentAnswersTest.setCorrectness(false);
                    studentAnswersTest.setSkipped(true);
                }
                studentAnswersTestRepository.delete(studentAnswersTest);
                studentAnswersTestRepository.save(studentAnswersTest);
                break;
            case "likedQuestions":
                studentAnswersTest = studentAnswersTestRepository.findBySchoolUserAndTestAndQuestion(loggedUser, t, q);
                UserLikedQuestion userLikedQuestion = userLikedQuestionRepository.findBySchoolUserAndQuestionIdAndTestId(loggedUser, questionId, testId);
                if (userLikedQuestion != null)
                    userLikedQuestionRepository.delete(userLikedQuestion);

                if (userLikedQuestion != null && isLiked)
                    userLikedQuestionRepository.save(userLikedQuestion);

                if (q != null && !answer.equals("SKIP")) {
                    studentAnswersTest.setCorrectness(q.getCorrectAnswer().equals(answer));
                    studentAnswersTest.setSkipped(false);
                } else {
                    studentAnswersTest.setCorrectness(false);
                    studentAnswersTest.setSkipped(true);
                }

                studentAnswersTestRepository.save(studentAnswersTest);
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
                studentAnswersTestRepository.save(studentAnswersTest);
                break;
        }

        return studentAnswersTest;
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
        List<StudentAnswersTest> list = studentAnswersTestRepository.findAllBySchoolUserAndTest(schoolUserRepository.findById(userId).orElse(null), testRepository.findById(testId).orElse(null));
        return list.stream().filter(l -> l.getCorrectness() && !l.getSkipped() && l.getTest().getDrivingCategory().contains("B")).toList();
    }

    public List<StudentAnswersTest> getInCorrectStudentAnswersTestByUserIdandTestId(Long userId, Long testId) {
        List<StudentAnswersTest> list = studentAnswersTestRepository.findAllBySchoolUserAndTest(schoolUserRepository.findById(userId).orElse(null), testRepository.findById(testId).orElse(null));
        return list.stream().filter(l -> !l.getCorrectness() && !l.getSkipped() && l.getTest().getDrivingCategory().contains("B")).toList();
    }

    public List<StudentAnswersTest> getSkippedStudentAnswersTestByUserIdandTestId(Long userId, Long testId) {
        List<StudentAnswersTest> list = studentAnswersTestRepository.findAllBySchoolUserAndTest(schoolUserRepository.findById(userId).orElse(null), testRepository.findById(testId).orElse(null));
        return list.stream().filter(l -> l.getSkipped() && l.getTest().getDrivingCategory().contains("B")).toList();
    }

    public List<Question> findQuestionsByUserIdAndTestId(Long userId, Long testId) {
        return studentAnswersTestRepository.findQuestionsByUserIdAndTestId(userId, testId);
    }
}
