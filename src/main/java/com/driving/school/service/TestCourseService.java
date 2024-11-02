package com.driving.school.service;

import com.driving.school.model.TestCourse;
import com.driving.school.repository.TestCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestCourseService {

    private final TestCourseRepository testCourseRepository;

    @Autowired
    public TestCourseService(TestCourseRepository testCourseRepository) {
        this.testCourseRepository = testCourseRepository;
    }

    public List<TestCourse> getAllTestCourses() {
        return testCourseRepository.findAll();
    }

    public Optional<TestCourse> getTestCourseById(Long id) {
        return testCourseRepository.findById(id);
    }

    public TestCourse createTestCourse(TestCourse testCourse) {
        return testCourseRepository.save(testCourse);
    }

    public void updateTestCourse(Long id, TestCourse testCourse) {
        Optional<TestCourse> optionalTestCourse = testCourseRepository.findById(id);

        if (optionalTestCourse.isPresent()) {
            TestCourse existingTestCourse = optionalTestCourse.get();
            existingTestCourse.setTestDate(testCourse.getTestDate());
            existingTestCourse.setInstructorComment(testCourse.getInstructorComment());
            existingTestCourse.setTestType(testCourse.getTestType());
            existingTestCourse.setTestResult(testCourse.getTestResult());
        }
    }

    public void deleteTestCourse(Long id) {
        Optional<TestCourse> testCourse = testCourseRepository.findById(id);
        testCourse.ifPresent(testCourseRepository::delete);
    }
}
