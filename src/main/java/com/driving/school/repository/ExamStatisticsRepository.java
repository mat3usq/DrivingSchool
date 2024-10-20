package com.driving.school.repository;

import com.driving.school.model.ExamStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamStatisticsRepository extends JpaRepository<ExamStatistics, Long> {
    Optional<ExamStatistics> findByCategory(String categoryName);
}