package com.driving.school.repository;

import com.driving.school.model.Course;
import com.driving.school.model.SchoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByMentorShipId(Long mentorShipId);

    @Query("SELECT c FROM Course c WHERE c.mentorShip.student = :student")
    List<Course> findCoursesByStudent(@Param("student") SchoolUser student);
}
