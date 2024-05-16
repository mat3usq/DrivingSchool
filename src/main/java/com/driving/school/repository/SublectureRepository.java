package com.driving.school.repository;

import com.driving.school.model.Sublecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SublectureRepository extends JpaRepository<Sublecture, Long> {
    List<Sublecture> findAllByOrderByOrderIndex();

    @Query("SELECT COALESCE(MAX(s.orderIndex), 0) FROM Sublecture s")
    Integer findMaxOrderIndex();
}