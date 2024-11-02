package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.Course;
import com.driving.school.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void createCourse(Course course) {
        courseRepository.save(course);
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void updateCourse(Long id, Course courseDetails) {
        courseRepository.findById(id).map(course -> {
            course.setDescription(courseDetails.getDescription());
            course.setCategory(courseDetails.getCategory());
            course.setDuration(courseDetails.getDuration());
            course.setPassed(courseDetails.getPassed());

            if (courseDetails.getPassed().equals(Constants.COURSE_PASSED) ||
                    courseDetails.getPassed().equals(Constants.COURSE_FAILED))
                course.setEndAt(LocalDate.now());
            else if (courseDetails.getPassed().equals(Constants.COURSE_NOTSPECIFIED))
                course.setEndAt(null);

            return courseRepository.save(course);
        });
    }

    public void deleteCourse(Long id) {
        Optional<Course> course = courseRepository.findById(id);
        course.ifPresent(courseRepository::delete);
    }
}
