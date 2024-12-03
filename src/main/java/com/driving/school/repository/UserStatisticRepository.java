package com.driving.school.repository;

import com.driving.school.model.Category;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.UserStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatisticRepository extends JpaRepository<UserStatistic, Long> {
    
    List<UserStatistic> findBySchoolUser(SchoolUser schoolUser);
    
    List<UserStatistic> findByCategory(Category category);
    
    Optional<UserStatistic> findBySchoolUserAndCategory(SchoolUser schoolUser, Category category);
}
