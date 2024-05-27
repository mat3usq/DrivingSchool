package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentInstructorRepository extends JpaRepository<StudentInstructor, Long> {
    List<StudentInstructor> findByStudentId(Long studentId);
    boolean existsByStudentAndInstructor(SchoolUser student, SchoolUser instructor);
}