package com.driving.school.repository;

import com.driving.school.model.InstructionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InstructionEventRepository extends JpaRepository<InstructionEvent, Long> {
    List<InstructionEvent> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    List<InstructionEvent> findByStartTimeBetweenAndInstructorId(LocalDateTime startTime, LocalDateTime endTime, Long instructorId);
}