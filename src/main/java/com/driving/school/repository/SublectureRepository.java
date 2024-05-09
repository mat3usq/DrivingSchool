package com.driving.school.repository;

import com.driving.school.model.Sublecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SublectureRepository extends JpaRepository<Sublecture, Long> {
}