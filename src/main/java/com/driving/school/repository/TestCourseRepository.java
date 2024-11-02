package com.driving.school.repository;

import com.driving.school.model.TestCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCourseRepository extends JpaRepository<TestCourse, Long> {
}