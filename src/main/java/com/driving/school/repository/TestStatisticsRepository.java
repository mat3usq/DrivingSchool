package com.driving.school.repository;

import com.driving.school.model.TestStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestStatisticsRepository extends JpaRepository<TestStatistics, Long> {
    Optional<TestStatistics> findByTestId(Long testId);
}