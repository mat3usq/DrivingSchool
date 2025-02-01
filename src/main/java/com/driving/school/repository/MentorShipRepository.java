package com.driving.school.repository;

import com.driving.school.model.MentorShip;
import com.driving.school.model.SchoolUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorShipRepository extends JpaRepository<MentorShip, Long> {
    List<MentorShip> findByStudentIdOrderByStatusAsc(Long studentId);
    List<MentorShip> findByInstructorIdOrderByStatusAsc(Long instructorId);
    List<MentorShip> findAllByInstructorAndStatus(SchoolUser instructor, String status);
    Page<MentorShip> findByInstructorIdOrderByStatusAsc(Long instructorId, Pageable pageable);
    MentorShip findByStudentIdAndInstructorIdAndStatus(Long studentId, Long instructorId, String status);
    MentorShip findByStudentIdAndInstructorId(Long studentId, Long instructorId);
    boolean existsByStudentAndInstructor(SchoolUser student, SchoolUser instructor);
    boolean existsByStudentAndInstructorAndStatus(SchoolUser student, SchoolUser instructor, String status);
}