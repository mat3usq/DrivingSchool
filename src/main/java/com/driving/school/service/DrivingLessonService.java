package com.driving.school.service;

import com.driving.school.model.DrivingLesson;
import com.driving.school.repository.DrivingLessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class DrivingLessonService {


    @Autowired
    private DrivingLessonRepository drivingLessonRepository;


    public DrivingLesson createDrivingLesson(DrivingLesson drivingLesson) {
        return drivingLessonRepository.save(drivingLesson);
    }


    public Optional<DrivingLesson> getDrivingLessonById(Long id) {
        return drivingLessonRepository.findById(id);
    }


    public List<DrivingLesson> getAllDrivingLessons() {
        return drivingLessonRepository.findAll();
    }

    // Update an existing DrivingLesson
    public DrivingLesson updateDrivingLesson(Long id, DrivingLesson drivingLessonDetails) {
        Optional<DrivingLesson> optionalDrivingLesson = drivingLessonRepository.findById(id);

        if (optionalDrivingLesson.isPresent()) {
            DrivingLesson drivingLesson = optionalDrivingLesson.get();
            drivingLesson.setStartTime(drivingLessonDetails.getStartTime());
            drivingLesson.setEndTime(drivingLessonDetails.getEndTime());
            drivingLesson.setTime(drivingLessonDetails.getTime());
            drivingLesson.setSchoolUser(drivingLessonDetails.getSchoolUser());
            drivingLesson.setStudentId(drivingLessonDetails.getStudentId());
            drivingLesson.setStatus(drivingLessonDetails.getStatus());
            return drivingLessonRepository.save(drivingLesson);
        } else {
            throw new RuntimeException("DrivingLesson not found with id " + id);
        }
    }

    public void deleteDrivingLesson(Long id) {
        drivingLessonRepository.deleteById(id);
    }

    public List<DrivingLesson> findDrivingLessonsByTimeRange(LocalDate startTime, LocalDate endTime) {
        return drivingLessonRepository.findByStartTimeBetween(startTime, endTime);
    }

    public List<DrivingLesson> findDrivingLessonsByTimeRangeAndInstructor(LocalDate startTime, LocalDate endTime, Long schoolUserId) {
        return drivingLessonRepository.findByStartTimeBetweenAndSchoolUserId(startTime, endTime, schoolUserId);
    }

}
