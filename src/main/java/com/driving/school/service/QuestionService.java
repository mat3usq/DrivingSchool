package com.driving.school.service;


import com.driving.school.model.Question;
import com.driving.school.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }


    public Question save(Question question) {
        return questionRepository.save(question);
    }


    public List<Question> findAll() {
        return questionRepository.findAll();
    }
}
