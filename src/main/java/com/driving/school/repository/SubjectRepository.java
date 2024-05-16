package com.driving.school.repository;

import com.driving.school.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByOrderByOrderIndex();

    @Query("SELECT COALESCE(MAX(s.orderIndex), 0) FROM Subject s")
    Integer findMaxOrderIndex();
}