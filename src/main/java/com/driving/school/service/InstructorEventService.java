package com.driving.school.service;


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
            instructionEvent.setInstructor(instructionEventDetails.getInstructor());
            instructionEvent.setStudents(instructionEventDetails.getStudents());
            instructionEvent.setStatus(instructionEventDetails.getStatus());
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
            instructionEventRepository.save(instructionEvent);
        } else {
            throw new RuntimeException("InstructionEvent or Student not found with the provided IDs");
        }
    }
}
