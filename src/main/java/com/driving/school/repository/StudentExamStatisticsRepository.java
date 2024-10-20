package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentExamStatistics;
import com.driving.school.model.StudentTestStatistics;
import com.driving.school.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentExamStatisticsRepository extends JpaRepository<StudentExamStatistics, Long> {
    StudentExamStatistics findBySchoolUserAndCategory(SchoolUser schoolUser, String category);

    @Query("SELECT ses.category, " +
            "AVG(ses.averagePoints), " +
            "SUM(ses.numberOfSolvedExams), " +
            "SUM(ses.numberOfPassedExams), " +
            "AVG(ses.averageExamsDuration), " +
            "AVG(ses.averageTimePerQuestions), " +
            "SUM(ses.numberOfQuestionsAnsweredCorrectly), " +
            "SUM(ses.numberOfQuestionsAnsweredInCorrectly), " +
            "SUM(ses.numberOfQuestionsSkipped) " +
            "FROM StudentExamStatistics ses " +
            "GROUP BY ses.category")
    List<Object[]> aggregateExamStatistics();
}
