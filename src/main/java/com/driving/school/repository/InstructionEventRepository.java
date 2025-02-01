package com.driving.school.repository;

import com.driving.school.model.InstructionEvent;
import com.driving.school.model.SchoolUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InstructionEventRepository extends JpaRepository<InstructionEvent, Long> {
    List<InstructionEvent> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    List<InstructionEvent> findByStartTimeBetweenAndInstructorId(LocalDateTime startTime, LocalDateTime endTime, Long instructorId);
    Optional<InstructionEvent> findById(Long id);
    List<InstructionEvent> findByInstructorAndStartTimeBetween(SchoolUser instructor, LocalDateTime start, LocalDateTime end);

}