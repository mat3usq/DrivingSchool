package com.driving.school.repository;

import com.driving.school.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q JOIN q.tests t WHERE t.id = :testId")
    List<Question> findAllByTestId(@Param("testId") Long testId);
}