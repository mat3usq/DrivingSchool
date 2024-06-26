package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentInstructorRepository extends JpaRepository<StudentInstructor, Long> {
    List<StudentInstructor> findByStudentId(Long studentId);
    List<StudentInstructor> findByInstructorId(Long instructorId);
    StudentInstructor findByStudentIdAndInstructorId(Long studentId, Long instructorId);
    boolean existsByStudentAndInstructor(SchoolUser student, SchoolUser instructor);
}