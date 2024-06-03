package com.driving.school.service;


import com.driving.school.model.Question;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.repository.QuestionRepository;
import com.driving.school.repository.StudentAnswersTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final StudentAnswersTestRepository studentAnswersTestRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, StudentAnswersTestRepository studentAnswersTestRepository) {
        this.questionRepository = questionRepository;
        this.studentAnswersTestRepository = studentAnswersTestRepository;
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public List<Question> findAllByCategoryAndTestId(String category, Long testId) {
        return questionRepository.findAllByTestId(testId).stream().filter(q -> q.getDrivingCategory().contains(category)).toList();
    }

    public Question getNextQuestion(Long testId, Long userId) {
        List<Question> allQuestions = findAllByCategoryAndTestId("B", testId);
        List<Question> usersQuestions = studentAnswersTestRepository.findQuestionsByUserIdAndTestId(userId, testId);
        List<Question> remainingQuestions = new ArrayList<>(allQuestions);
        remainingQuestions.removeAll(usersQuestions);
        Question nextQuestion = remainingQuestions.getFirst();
        nextQuestion.setQuestionNumber(allQuestions.size() - remainingQuestions.size());
        return nextQuestion;
    }
}
