package com.driving.school.service;


import com.driving.school.model.Constants;
import com.driving.school.model.InstructionEvent;
import com.driving.school.model.MentorShip;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.InstructionEventRepository;
import com.driving.school.repository.SchoolUserRepository;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InstructorEventService {

    @Autowired
    InstructionEventRepository instructionEventRepository;

    @Autowired
    SchoolUserRepository schoolUserRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MentorShipService mentorShipService;

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
            if (instructionEventDetails.getEventCapacity() == null) {
                instructionEventDetails.setEventCapacity(0);
                instructionEventDetails.setAvailableEventSlots(0);
            }

            if (instructionEvent.getEventCapacity() == null) {
                instructionEvent.setEventCapacity(0);
                instructionEvent.setAvailableEventSlots(0);
            }

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

            instructionEvent.getStudents().forEach(s -> {
                notificationService.cancelReminderForEvent(instructionEvent.getId(), s.getId());
                try {
                    notificationService.scheduleReminderForEvent(instructionEvent, s);
                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            });

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

    public List<InstructionEvent> getEvents(SchoolUser user, YearMonth yearMonth, Authentication authentication) {
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<InstructionEvent> events = new ArrayList<>();
        if (authentication.getAuthorities().toArray()[0].toString().equals("ROLE_STUDENT")) {
            List<MentorShip> mentorShips = mentorShipService.findByStudentId(user.getId());
            for (MentorShip ms : mentorShips) {
                if (ms.getStatus().equals(Constants.ACTIVE)) {
                    List<InstructionEvent> eventsByInstructor = findInstructionEventsByTimeRangeAndInstructor(startOfMonth, endOfMonth, ms.getInstructor().getId());
                    events.addAll(eventsByInstructor);
                }
            }
        } else if (authentication.getAuthorities().toArray()[0].toString().equals("ROLE_INSTRUCTOR")) {
            events = findInstructionEventsByTimeRangeAndInstructor(startOfMonth, endOfMonth, user.getId());
        } else if (authentication.getAuthorities().toArray()[0].toString().equals("ROLE_ADMIN"))
            events = findInstructionEventsByTimeRange(startOfMonth, endOfMonth);

        events.forEach(e -> {
            if (e.getStudents() != null)
                e.setIsAssigned(e.getStudents().stream().anyMatch(s -> Objects.equals(s.getId(), user.getId())));
        });

        return events;
    }
}
