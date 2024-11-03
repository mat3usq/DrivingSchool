package com.driving.school.service;

import com.driving.school.model.DrivingSession;
import com.driving.school.model.Course;
import com.driving.school.repository.CourseRepository;
import com.driving.school.repository.DrivingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrivingSessionService {

    private final DrivingSessionRepository drivingSessionRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public DrivingSessionService(DrivingSessionRepository drivingSessionRepository, CourseRepository courseRepository) {
        this.drivingSessionRepository = drivingSessionRepository;
        this.courseRepository = courseRepository;
    }

    public void createDrivingSession(DrivingSession drivingSession, Course course) {
        course.setSummaryDurationHours(course.getSummaryDurationHours() == null ? 0 : course.getSummaryDurationHours() + drivingSession.getDurationHours());
        drivingSession.setCourse(course);
        drivingSessionRepository.save(drivingSession);
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

            Course course = drivingSession.getCourse();
            double totalDuration = course.getDrivingSessions().stream()
                    .mapToDouble(DrivingSession::getDurationHours)
                    .sum();
            course.setSummaryDurationHours(totalDuration);

            courseRepository.save(course);
        } else {
            throw new RuntimeException("DrivingSession not found with id " + id);
        }
    }


    public void deleteDrivingSession(Long id) {
        Optional<DrivingSession> optionalDrivingSession = drivingSessionRepository.findById(id);
        if (optionalDrivingSession.isPresent()) {
            Optional<Course> courseOptional = courseRepository.findById(optionalDrivingSession.get().getCourse().getId());
            if (courseOptional.isPresent()) {
                Course course = courseOptional.get();
                course.setSummaryDurationHours(course.getSummaryDurationHours() - optionalDrivingSession.get().getDurationHours());
                course.getDrivingSessions().remove(optionalDrivingSession.get());
                courseRepository.save(course);
            }
        }
    }
}
