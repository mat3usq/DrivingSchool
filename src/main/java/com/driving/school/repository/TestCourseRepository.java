package com.driving.school.repository;

import com.driving.school.model.Course;
import com.driving.school.model.TestCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCourseRepository extends JpaRepository<TestCourse, Long> {
    List<TestCourse> findAllByCourse(Course course);
}