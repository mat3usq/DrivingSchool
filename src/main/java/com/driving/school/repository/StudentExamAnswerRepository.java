package com.driving.school.repository;

import com.driving.school.model.StudentExam;
import com.driving.school.model.StudentExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentExamAnswerRepository extends JpaRepository<StudentExamAnswer, Long> {
    List<StudentExamAnswer> findStudentExamAnswerByStudentExam(StudentExam studentExam);
}