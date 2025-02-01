package com.driving.school.repository;

import com.driving.school.model.CommentCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentCourseRepository extends JpaRepository<CommentCourse, Long> {
}