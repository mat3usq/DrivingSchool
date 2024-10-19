package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentTestStatistics;
import com.driving.school.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTestStatisticsRepository extends JpaRepository<StudentTestStatistics, Long> {
    StudentTestStatistics findBySchoolUserAndTest(SchoolUser schoolUser, Test test);
}
