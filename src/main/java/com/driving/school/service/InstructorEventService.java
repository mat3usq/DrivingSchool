package com.driving.school.service;


import com.driving.school.model.Constants;
import com.driving.school.model.InstructionEvent;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.InstructionEventRepository;
import com.driving.school.repository.SchoolUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InstructorEventService {

    @Autowired
    InstructionEventRepository instructionEventRepository;

    @Autowired
    SchoolUserRepository schoolUserRepository;
    @Autowired
    private NotificationService notificationService;

    public InstructorEventService(InstructionEventRepository instructionEventRepository, SchoolUserRepository schoolUserRepository) {
        this.instructionEventRepository = instructionEventRepository;
        this.schoolUserRepository = schoolUserRepository;
    }

    public InstructionEvent findById(Long id) {
        return instructionEventRepository.findById(id).orElse(null);
    }

    public Optional<InstructionEvent> getInstructionEventById(Long id) {
        return instructionEventRepository.findById(id);
    }

    public List<InstructionEvent> getAllInstructionEvents() {
        return instructionEventRepository.findAll();
    }

    public InstructionEvent updateInstructionEvent(Long id, InstructionEvent instructionEventDetails) {
        Optional<InstructionEvent> optionalInstructionEvent = instructionEventRepository.findById(id);

        if (optionalInstructionEvent.isPresent()) {
            InstructionEvent instructionEvent = optionalInstructionEvent.get();
            instructionEvent.setSubject(instructionEventDetails.getSubject());
            instructionEvent.setEventType(instructionEventDetails.getEventType());
            instructionEvent.setStartTime(instructionEventDetails.getStartTime());
            instructionEvent.setEndTime(instructionEventDetails.getEndTime());
            Integer diffSlots = instructionEvent.getEventCapacity() - instructionEvent.getAvailableEventSlots();
            if (instructionEvent.getEventCapacity() > instructionEventDetails.getEventCapacity())
                if (diffSlots > instructionEventDetails.getEventCapacity()) {
                    instructionEvent.setEventCapacity(diffSlots);
                    instructionEvent.setAvailableEventSlots(0);
                } else {
                    instructionEvent.setEventCapacity(instructionEventDetails.getEventCapacity());
                    instructionEvent.setAvailableEventSlots(instructionEventDetails.getEventCapacity() - diffSlots);
                }
            else {
                diffSlots = instructionEventDetails.getEventCapacity() - instructionEvent.getEventCapacity();
                instructionEvent.setEventCapacity(instructionEventDetails.getEventCapacity());
                instructionEvent.setAvailableEventSlots(instructionEvent.getAvailableEventSlots() + diffSlots);
            }

            notificationService.sendNotificationToUsersAreAssignedWhenInstructorUpdateEvent(instructionEvent);

            return instructionEventRepository.save(instructionEvent);
        } else {
            throw new RuntimeException("InstructionEvent not found with id " + id);
        }
    }

    public void deleteInstructionEvent(Long id) {
        instructionEventRepository.deleteById(id);
    }

    public List<InstructionEvent> findInstructionEventsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return instructionEventRepository.findByStartTimeBetween(startTime, endTime);
    }

    public List<InstructionEvent> findInstructionEventsByTimeRangeAndInstructor(LocalDateTime startTime, LocalDateTime endTime, Long instructorId) {
        return instructionEventRepository.findByStartTimeBetweenAndInstructorId(startTime, endTime, instructorId);
    }

    public void addStudentToInstructionEvent(Long eventId, Long studentId) {
        Optional<InstructionEvent> optionalInstructionEvent = instructionEventRepository.findById(eventId);
        Optional<SchoolUser> optionalStudent = schoolUserRepository.findById(studentId);

        if (optionalInstructionEvent.isPresent() && optionalStudent.isPresent()) {
            InstructionEvent instructionEvent = optionalInstructionEvent.get();
            SchoolUser student = optionalStudent.get();

            instructionEvent.getStudents().add(student);
            instructionEventRepository.save(instructionEvent);
        } else {
            throw new RuntimeException("InstructionEvent or Student not found with the provided IDs");
        }
    }

    public void removeStudentFromInstructionEvent(Long eventId, Long studentId) {
        Optional<InstructionEvent> optionalInstructionEvent = instructionEventRepository.findById(eventId);
        Optional<SchoolUser> optionalStudent = schoolUserRepository.findById(studentId);

        if (optionalInstructionEvent.isPresent() && optionalStudent.isPresent()) {
            InstructionEvent instructionEvent = optionalInstructionEvent.get();
            SchoolUser student = optionalStudent.get();

            instructionEvent.getStudents().remove(student);
            instructionEvent.setAvailableEventSlots(instructionEvent.getAvailableEventSlots() + 1);
            instructionEventRepository.save(instructionEvent);
        } else {
            throw new RuntimeException("InstructionEvent or Student not found with the provided IDs");
        }
    }
}
