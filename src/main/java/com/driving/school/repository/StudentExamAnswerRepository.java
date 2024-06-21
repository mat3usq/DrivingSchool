package com.driving.school.repository;

import com.driving.school.model.StudentExamAnswer;
import com.driving.school.model.StudentExamAnswerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentExamAnswerRepository extends JpaRepository<StudentExamAnswer, StudentExamAnswerId> {
}