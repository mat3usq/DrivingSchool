package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentTestStatistics;
import com.driving.school.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentTestStatisticsRepository extends JpaRepository<StudentTestStatistics, Long> {
    StudentTestStatistics findBySchoolUserAndTest(SchoolUser schoolUser, Test test);

    @Query("SELECT sts.test.id, " +
            "SUM(sts.numberOfQuestionsSolved), " +
            "SUM(sts.numberOfQuestionsAnsweredCorrectly), " +
            "SUM(sts.numberOfQuestionsAnsweredInCorrectly), " +
            "SUM(sts.numberOfQuestionsSkipped), " +
            "AVG(sts.averageDurationOfAnswers) " +
            "FROM StudentTestStatistics sts " +
            "GROUP BY sts.test.id")
    List<Object[]> aggregateTestStatistics();


    @Query("SELECT SUM(sts.numberOfQuestionsSolved), " +
            "SUM(sts.numberOfQuestionsAnsweredCorrectly), " +
            "SUM(sts.numberOfQuestionsAnsweredInCorrectly), " +
            "SUM(sts.numberOfQuestionsSkipped), " +
            "AVG(sts.averageDurationOfAnswers) " +
            "FROM StudentTestStatistics sts " +
            "JOIN sts.test t " +
            "WHERE t.drivingCategory = :category AND sts.schoolUser = :schoolUser " +
            "GROUP BY t.drivingCategory")
    List<Object[]> aggregateCategoryTestStatisticsByUser(String category, SchoolUser schoolUser);
}
