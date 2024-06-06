package com.driving.school.service;


import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.model.Test;
import com.driving.school.repository.QuestionRepository;
import com.driving.school.repository.StudentAnswersTestRepository;
import com.driving.school.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentAnswersTestService {
    private final StudentAnswersTestRepository studentAnswersTestRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public StudentAnswersTestService(StudentAnswersTestRepository studentAnswersTestRepository, TestRepository testRepository, QuestionRepository questionRepository) {
        this.studentAnswersTestRepository = studentAnswersTestRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
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

    public StudentAnswersTest save(SchoolUser loggedInUser, Long testId, Long questionId, String answer) {
        StudentAnswersTest studentAnswersTest = new StudentAnswersTest();
        studentAnswersTest.setSchoolUser(loggedInUser);
        studentAnswersTest.setTest(testRepository.findById(testId).orElse(null));
        Question q = questionRepository.findById(questionId).orElse(null);
        studentAnswersTest.setQuestion(q);
        if (q != null && !answer.equals("SKIP"))
            studentAnswersTest.setCorrectness(q.getCorrectAnswer().equals(answer));
        else
            studentAnswersTest.setCorrectness(false);
        return studentAnswersTestRepository.save(studentAnswersTest);
    }

    public void setStatisticForTest(List<Test> tests, Long userId) {
        tests.forEach(test -> {
            if(test.getNumberQuestions() != 0) {
                double answers = (double) studentAnswersTestRepository.countCorrectAnswersByUserIdAndTestId(userId, test.getId());
                double questions = (double) test.getNumberQuestions();
                double result = answers / questions * 100;
                test.setCounter((int) result);
            }
            else test.setCounter(0);
        });
    }
}
