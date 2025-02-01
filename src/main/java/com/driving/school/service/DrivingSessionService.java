package com.driving.school.service;

import com.driving.school.model.DrivingSession;
import com.driving.school.model.Course;
import com.driving.school.model.TestCourse;
import com.driving.school.repository.CourseRepository;
import com.driving.school.repository.DrivingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DrivingSessionService {

    private final DrivingSessionRepository drivingSessionRepository;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;

    @Autowired
    public DrivingSessionService(DrivingSessionRepository drivingSessionRepository, CourseRepository courseRepository, NotificationService notificationService) {
        this.drivingSessionRepository = drivingSessionRepository;
        this.courseRepository = courseRepository;
        this.notificationService = notificationService;
    }

    public void createDrivingSession(DrivingSession drivingSession, Course course) {
        drivingSession.setCourse(course);
        drivingSessionRepository.save(drivingSession);
        updateSummaryHoursInCourse(course);
    }

    public Optional<DrivingSession> getDrivingSessionById(Long id) {
        return drivingSessionRepository.findById(id);
    }

    public List<DrivingSession> getAllDrivingSessions() {
        return drivingSessionRepository.findAll();
    }

    public void updateDrivingSession(Long id, DrivingSession drivingSessionDetails) {
        Optional<DrivingSession> optionalDrivingSession = drivingSessionRepository.findById(id);

        if (optionalDrivingSession.isPresent()) {
            DrivingSession drivingSession = optionalDrivingSession.get();

            drivingSession.setDurationHours(drivingSessionDetails.getDurationHours());
            drivingSession.setInstructorComment(drivingSessionDetails.getInstructorComment());

            drivingSessionRepository.save(drivingSession);
            updateSummaryHoursInCourse(drivingSession.getCourse());
        }
    }

    public void deleteDrivingSession(Long id) {
        Optional<DrivingSession> drivingSession = drivingSessionRepository.findById(id);

        if (drivingSession.isPresent()) {
            drivingSessionRepository.delete(drivingSession.get());
            updateSummaryHoursInCourse(drivingSession.get().getCourse());
        }
    }

    private void updateSummaryHoursInCourse(Course course) {
        double totalDuration = Optional.ofNullable(course.getDrivingSessions())
                .orElse(Collections.emptyList())
                .stream()
                .mapToDouble(session -> {
                    Double duration = session.getDurationHours();
                    return duration != null ? duration : 0.0;
                })
                .sum();

        course.setSummaryDurationHours(totalDuration);
        courseRepository.save(course);
    }


    public void instructorCreateDrivingSession(DrivingSession newDrivingSession, Course course) {
        createDrivingSession(newDrivingSession, course);
        notificationService.sendNotificationWhenInstructorCreateDrivingSession(newDrivingSession);
    }
}
