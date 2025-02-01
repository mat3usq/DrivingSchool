package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.Course;
import com.driving.school.model.MentorShip;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.MentorShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MentorShipService {
    private final NotificationService notificationService;
    MentorShipRepository mentorShipRepository;

    @Autowired
    public MentorShipService(MentorShipRepository mentorShipRepository, NotificationService notificationService) {
        this.mentorShipRepository = mentorShipRepository;
        this.notificationService = notificationService;
    }

    public List<MentorShip> findByStudentId(Long studentId) {
        return mentorShipRepository.findByStudentIdOrderByStatusAsc(studentId);
    }

    public List<MentorShip> findByInstructorId(Long studentId) {
        return mentorShipRepository.findByInstructorIdOrderByStatusAsc(studentId);
    }

    public Page<MentorShip> findByInstructorId(Long instructorId, Pageable pageable) {
        return mentorShipRepository.findByInstructorIdOrderByStatusAsc(instructorId, pageable);
    }

    public boolean existsByStudentAndInstructor(SchoolUser student, SchoolUser instructor) {
        return mentorShipRepository.existsByStudentAndInstructor(student, instructor);
    }

    public MentorShip createMentorShip(MentorShip mentorShip) {
        return mentorShipRepository.save(mentorShip);
    }

    public Optional<MentorShip> getMentorShipById(Long id) {
        return mentorShipRepository.findById(id);
    }

    public List<MentorShip> getAllMentorShips() {
        return mentorShipRepository.findAll();
    }

    public MentorShip updateMentorShips(Long id, MentorShip mentorShipDetails) {
        Optional<MentorShip> optionalMentorShip = mentorShipRepository.findById(id);

        if (optionalMentorShip.isPresent()) {
            MentorShip mentorShip = optionalMentorShip.get();
            mentorShip.setStudent(mentorShipDetails.getStudent());
            mentorShip.setInstructor(mentorShipDetails.getInstructor());
            return mentorShipRepository.save(mentorShip);
        } else {
            throw new RuntimeException("MentorShip not found with id " + id);
        }
    }

    public void deleteMentorShipById(Long id) {
        mentorShipRepository.deleteById(id);
    }

    public boolean createMentorShipWithStatus(SchoolUser student, SchoolUser instructor, String status) {
        boolean existsInProgress = mentorShipRepository.existsByStudentAndInstructorAndStatus(student, instructor, Constants.ACTIVE);
        boolean existsInPending = mentorShipRepository.existsByStudentAndInstructorAndStatus(student, instructor, Constants.PENDING);

        if (!existsInProgress && !existsInPending) {
            MentorShip mentorShip = new MentorShip();
            mentorShip.setStudent(student);
            mentorShip.setInstructor(instructor);
            mentorShip.setStatus(status);
            mentorShipRepository.save(mentorShip);

            return true;
        }

        return false;
    }


    public Map<String, Set<String>> getStudentCategories(Long studentId) {
        List<MentorShip> mentorShips = mentorShipRepository.findByStudentIdOrderByStatusAsc(studentId);

        Set<String> activeCategories = new HashSet<>();
        Set<String> completedCategories = new HashSet<>();

        for (MentorShip mentorShip : mentorShips) {
            String status = mentorShip.getStatus();
            Set<Course> courses = mentorShip.getStudentCourses();

            for (Course course : courses) {
                if (course.getCategory() != null) {
                    String categoryName = course.getCategory().getNameCategory();

                    if (Constants.COMPLETED.equalsIgnoreCase(status)) {
                        completedCategories.add(categoryName);
                    } else {
                        activeCategories.add(categoryName);
                    }
                }
            }
        }

        Map<String, Set<String>> categoriesMap = new HashMap<>();
        categoriesMap.put("active", activeCategories);
        categoriesMap.put("completed", completedCategories);

        return categoriesMap;
    }

    public void acceptStudent(Long mentorShipId) {
        Optional<MentorShip> opt = mentorShipRepository.findById(mentorShipId);
        if (opt.isPresent()) {
            MentorShip mentorShip = opt.get();
            mentorShip.setStatus(Constants.ACTIVE);
            mentorShipRepository.save(mentorShip);
        }
    }

    public void finishMentorShip(Long mentorShipId) {
        Optional<MentorShip> opt = mentorShipRepository.findById(mentorShipId);
        if (opt.isPresent()) {
            MentorShip mentorShip = opt.get();
            mentorShip.setStatus(Constants.COMPLETED);
            mentorShip.setEndAt(LocalDateTime.now());
            mentorShipRepository.save(mentorShip);
        }
    }

    public void backToActiveMentorShip(Long mentorShipId) {
        Optional<MentorShip> opt = mentorShipRepository.findById(mentorShipId);
        if (opt.isPresent()) {
            MentorShip mentorShip = opt.get();
            mentorShip.setStatus(Constants.ACTIVE);
            mentorShip.setEndAt(null);
            mentorShipRepository.save(mentorShip);
        }
    }

    public boolean studentAssignsToInstructor(SchoolUser student, SchoolUser instructor) {
        boolean result = createMentorShipWithStatus(student, instructor, Constants.PENDING);
        if (result)
            notificationService.sendNotificationWhenStudentAssignsToInstructor(student, instructor);
        return result;
    }

    public void studentCancelMentorshipWithInstructor(MentorShip mentorShip) {
        deleteMentorShipById(mentorShip.getId());
        notificationService.sendNotificationWhenStudentCancelMentorship(mentorShip.getStudent(), mentorShip.getInstructor());
    }

    public boolean instructorCreateMentorshipWithStudent(SchoolUser student, SchoolUser instructor) {
        boolean result = createMentorShipWithStatus(student, instructor, Constants.ACTIVE);
        if (result)
            notificationService.sendNotificationWhenInstructorCreateMentorshipWithStudent(student, instructor);
        return result;
    }

    public void instructorAcceptMentorshipWithStudent(MentorShip mentorShip) {
        acceptStudent(mentorShip.getId());
        notificationService.sendNotificationWhenInstructorAcceptMentorshipWithStudent(mentorShip.getStudent(), mentorShip.getInstructor());
    }

    public void instructorCancelMentorshipWithStudent(MentorShip mentorShip) {
        deleteMentorShipById(mentorShip.getId());
        notificationService.sendNotificationWhenInstructorCancelMentorshipWithStudent(mentorShip.getStudent(), mentorShip.getInstructor());
    }

    public void instructorFinishMentorshipWithStudent(MentorShip mentorShip) {
        finishMentorShip(mentorShip.getId());
        notificationService.sendNotificationWhenInstructorFinishMentorshipWithStudent(mentorShip.getStudent(), mentorShip.getInstructor());
    }

    public void instructorBackToActiveMentorshipWithStudent(MentorShip mentorShip) {
        backToActiveMentorShip(mentorShip.getId());
        notificationService.sendNotificationWhenInstructorMakeMentorshipActiveAgainWithStudent(mentorShip.getStudent(), mentorShip.getInstructor());
    }
}
