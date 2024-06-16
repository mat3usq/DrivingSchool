package com.driving.school.service;


import com.driving.school.model.Question;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.repository.QuestionRepository;
import com.driving.school.repository.StudentAnswersTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        Question nextQuestion = new Question();
        remainingQuestions.removeAll(usersQuestions);
        if (remainingQuestions.isEmpty())
            if (allQuestions.isEmpty())
                nextQuestion.setQuestionNumber(0);
            else nextQuestion.setQuestionNumber(allQuestions.size() + 1);
        else {
            nextQuestion = remainingQuestions.getFirst();
            nextQuestion.setQuestionNumber(allQuestions.size() - remainingQuestions.size() + 1);
        }
        return nextQuestion;
    }

    public List<Question> getAllNoSpecialisticQuestionByCategory(String category){
        List<Question> allQuestion = findAll().stream().filter(q -> q.getDrivingCategory().contains(category)  && Boolean.FALSE.equals(q.getQuestionType())).toList();
        return allQuestion;
    }

    public List<Question> getAllSpecialisticQuestionByCategory(String category){
        List<Question> allQuestion = findAll().stream().filter(q -> q.getDrivingCategory().contains(category)  && Boolean.TRUE.equals(q.getQuestionType())).toList();
        return allQuestion;
    }

    public List<Question> getRandomNoSpecialistcQuestionsByCategory(String category, int numberOfQuestions) {
        List<Question> filteredQuestions = getAllNoSpecialisticQuestionByCategory(category);
        Collections.shuffle(filteredQuestions);
        return filteredQuestions.stream().limit(numberOfQuestions).collect(Collectors.toList());
    }

    public List<Question> getRandomSpecialistcQuestionsByCategory(String category, int numberOfQuestions) {
        List<Question> filteredQuestions = getAllSpecialisticQuestionByCategory(category);
        Collections.shuffle(filteredQuestions);
        return filteredQuestions.stream().limit(numberOfQuestions).collect(Collectors.toList());
    }
}
