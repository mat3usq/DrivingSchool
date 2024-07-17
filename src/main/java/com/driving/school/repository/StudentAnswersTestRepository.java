package com.driving.school.repository;

import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentAnswersTestRepository extends JpaRepository<StudentAnswersTest, Long> {
    @Query("SELECT sat.question FROM StudentAnswersTest sat WHERE sat.schoolUser.id = :userId AND sat.test.id = :testId")
    List<Question> findQuestionsByUserIdAndTestId(@Param("userId") Long userId, @Param("testId") Long testId);

    @Query("SELECT COUNT(sat) FROM StudentAnswersTest sat WHERE sat.schoolUser.id = :userId AND sat.test.id = :testId")
    Integer countAnswersByUserIdAndTestId(@Param("userId") Long userId, @Param("testId") Long testId);

    List<StudentAnswersTest> findAllBySchoolUserAndTest(SchoolUser schoolUser, Test test);

    StudentAnswersTest findBySchoolUserAndTestAndQuestion(SchoolUser schoolUser, Test test, Question question);
}