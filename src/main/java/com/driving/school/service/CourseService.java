package com.driving.school.service;

import com.driving.school.model.CommentCourse;
import com.driving.school.model.Constants;
import com.driving.school.model.Course;
import com.driving.school.repository.CommentCourseRepository;
import com.driving.school.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CommentCourseRepository commentCourseRepository;
    private final NotificationService notificationService;

    @Autowired
    public CourseService(CourseRepository courseRepository, CommentCourseRepository commentCourseRepository, NotificationService notificationService) {
        this.courseRepository = courseRepository;
        this.commentCourseRepository = commentCourseRepository;
        this.notificationService = notificationService;
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

    public void updateCourse(Long id, Course courseDetails, CommentCourse newCommentCourse) {
        courseRepository.findById(id).ifPresent(course -> {
            course.setDescription(courseDetails.getDescription());
            course.setCategory(courseDetails.getCategory());
            course.setDuration(courseDetails.getDuration());
            course.setPassed(courseDetails.getPassed());

            Map<Long, CommentCourse> editCommentsMap = courseDetails.getCommentCourses().stream()
                    .collect(Collectors.toMap(CommentCourse::getId, Function.identity()));

            course.getCommentCourses().forEach(existingComment -> {
                CommentCourse updatedComment = editCommentsMap.get(existingComment.getId());
                if (updatedComment != null) {
                    existingComment.setInstructorComment(updatedComment.getInstructorComment());
                }
            });

            if (newCommentCourse != null && newCommentCourse.getInstructorComment() != null
                    && !newCommentCourse.getInstructorComment().trim().isEmpty()) {
                newCommentCourse.setCourse(course);
                course.getCommentCourses().addFirst(newCommentCourse);
                commentCourseRepository.save(newCommentCourse);
            }

            switch (courseDetails.getPassed()) {
                case Constants.COURSE_PASSED:
                case Constants.COURSE_FAILED:
                    course.setEndAt(LocalDate.now());
                    break;
                case Constants.COURSE_NOTSPECIFIED:
                    course.setEndAt(null);
                    break;
            }

            courseRepository.save(course);
        });
    }

    public void deleteCourse(Long id) {
        Optional<Course> course = courseRepository.findById(id);
        course.ifPresent(courseRepository::delete);
    }

    public void instructorCreateNewCourse(Course course) {
        createCourse(course);
        notificationService.sendNotificationWhenInstructorCreateCourse(course);
    }
}
