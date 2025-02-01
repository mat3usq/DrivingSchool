package com.driving.school.repository;

import com.driving.school.model.TestStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestStatisticsRepository extends JpaRepository<TestStatistics, Long> {
    Optional<TestStatistics> findByTestId(Long testId);

    List<TestStatistics> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}