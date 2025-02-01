package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {
    List<StudentExam> findBySchoolUser(SchoolUser schoolUser);
}