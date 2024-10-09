package com.driving.school.service;


import com.driving.school.model.Test;
import com.driving.school.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;


    public Test saveTest(Test test) {
        return testRepository.save(test);
    }


    public Test getTestById(Long id) {
        return testRepository.findById(id).orElse(null);
    }


    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public List<Test> getAllTestsByCategory(String category) {
        return testRepository.findAll().stream().filter(t -> t.getDrivingCategory().equals(category)).toList();
    }

    public void deleteTestById(Long id) {
        testRepository.deleteById(id);
    }


    public Test updateTest(Long id, Test testDetails) {
        Optional<Test> optionalTest = testRepository.findById(id);
        if (optionalTest.isPresent()) {
            Test test = optionalTest.get();
            test.setName(testDetails.getName());
            test.setNumberQuestions(testDetails.getNumberQuestions());
            test.setImage(testDetails.getImage());
            test.setTestType(testDetails.getTestType());
            return testRepository.save(test);
        } else {
            return null;
        }
    }
}
