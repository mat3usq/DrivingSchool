package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentInstructorRepository extends JpaRepository<StudentInstructor, Long> {
    List<StudentInstructor> findByStudentIdOrderByStatusAsc(Long studentId);
    Page<StudentInstructor> findByInstructorIdOrderByStatusAsc(Long instructorId, Pageable pageable);
    StudentInstructor findByStudentIdAndInstructorIdAndStatus(Long studentId, Long instructorId, String status);
    StudentInstructor findByStudentIdAndInstructorId(Long studentId, Long instructorId);
    boolean existsByStudentAndInstructor(SchoolUser student, SchoolUser instructor);
    boolean existsByStudentAndInstructorAndStatus(SchoolUser student, SchoolUser instructor, String status);
}