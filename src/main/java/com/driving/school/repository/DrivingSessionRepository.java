package com.driving.school.repository;

import com.driving.school.model.DrivingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrivingSessionRepository extends JpaRepository<DrivingSession, Long> {
}