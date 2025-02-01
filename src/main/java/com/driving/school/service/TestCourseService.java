package com.driving.school.service;

import com.driving.school.model.Course;
import com.driving.school.model.TestCourse;
import com.driving.school.repository.CourseRepository;
import com.driving.school.repository.TestCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TestCourseService {

    private final TestCourseRepository testCourseRepository;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;

    @Autowired
    public TestCourseService(TestCourseRepository testCourseRepository,
                             CourseRepository courseRepository, NotificationService notificationService) {
        this.testCourseRepository = testCourseRepository;
        this.courseRepository = courseRepository;
        this.notificationService = notificationService;
    }

    public List<TestCourse> getAllTestCoursesByCourse(Course course) {
        return testCourseRepository.findAllByCourse(course);
    }

    public Optional<TestCourse> getTestCourseById(Long id) {
        return testCourseRepository.findById(id);
    }

    public void createTestCourse(TestCourse testCourse, Course course) {
        testCourse.setCourse(course);
        testCourseRepository.save(testCourse);
        updateAverageInCourse(course);
    }

    public void updateTestCourse(Long id, TestCourse testCourse) {
        Optional<TestCourse> optionalTestCourse = testCourseRepository.findById(id);

        if (optionalTestCourse.isPresent()) {
            TestCourse existingTestCourse = optionalTestCourse.get();
            existingTestCourse.setInstructorComment(testCourse.getInstructorComment());
            existingTestCourse.setTestType(testCourse.getTestType());
            existingTestCourse.setTestResult(testCourse.getTestResult());
            updateAverageInCourse(existingTestCourse.getCourse());
            testCourseRepository.save(existingTestCourse);
        }
    }

    public void deleteTestCourse(Long id) {
        Optional<TestCourse> testCourse = testCourseRepository.findById(id);

        if (testCourse.isPresent()) {
            testCourseRepository.delete(testCourse.get());
            updateAverageInCourse(testCourse.get().getCourse());
        }
    }

    private void updateAverageInCourse(Course course) {
        double averageResult = Optional.ofNullable(course.getTestCourses())
                .orElse(Collections.emptyList())
                .stream()
                .mapToDouble(testCourse -> {
                    Double result = testCourse.getTestResult();
                    return result != null ? result : 0.0;
                })
                .average()
                .orElse(0.0);

        course.setSummaryAverageResultTest(averageResult);
        courseRepository.save(course);
    }


    public void instructorCreateTestCourse(TestCourse newTestCourse, Course course) {
        createTestCourse(newTestCourse, course);
        notificationService.sendNotificationWhenInstructorCreateTestCourse(newTestCourse);
    }
}
