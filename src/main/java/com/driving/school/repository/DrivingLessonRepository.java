package com.driving.school.repository;

import com.driving.school.model.DrivingLesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DrivingLessonRepository extends JpaRepository<DrivingLesson, Long> {
    List<DrivingLesson> findByStartTimeBetween(LocalDate startTime, LocalDate endTime);
    List<DrivingLesson> findByStartTimeBetweenAndSchoolUserId(LocalDate startTime, LocalDate endTime, Long schoolUserId);



}