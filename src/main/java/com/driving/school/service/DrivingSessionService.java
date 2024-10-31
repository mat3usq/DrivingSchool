package com.driving.school.service;

import com.driving.school.model.DrivingSession;
import com.driving.school.model.Course;
import com.driving.school.repository.DrivingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class DrivingSessionService {

    private final DrivingSessionRepository drivingSessionRepository;

    @Autowired
    public DrivingSessionService(DrivingSessionRepository drivingSessionRepository) {
        this.drivingSessionRepository = drivingSessionRepository;
    }

    public DrivingSession createDrivingSession(DrivingSession drivingSession) {
        return drivingSessionRepository.save(drivingSession);
    }

    public Optional<DrivingSession> getDrivingSessionById(Long id) {
        return drivingSessionRepository.findById(id);
    }

    public List<DrivingSession> getAllDrivingSessions() {
        return drivingSessionRepository.findAll();
    }

    public DrivingSession updateDrivingSession(Long id, DrivingSession drivingSessionDetails) {
        Optional<DrivingSession> optionalDrivingSession = drivingSessionRepository.findById(id);

        if (optionalDrivingSession.isPresent()) {
            DrivingSession drivingSession = optionalDrivingSession.get();
            drivingSession.setSessionDate(drivingSessionDetails.getSessionDate());
            drivingSession.setDurationHours(drivingSessionDetails.getDurationHours());
            drivingSession.setInstructorComment(drivingSessionDetails.getInstructorComment());
            drivingSession.setCourse(drivingSessionDetails.getCourse());
            return drivingSessionRepository.save(drivingSession);
        } else {
            throw new RuntimeException("DrivingSession not found with id " + id);
        }
    }

    public void deleteDrivingSession(Long id) {
        drivingSessionRepository.deleteById(id);
    }
}
