package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.MentorShip;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.MentorShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MentorShipService {
    MentorShipRepository mentorShipRepository;

    @Autowired
    public MentorShipService(MentorShipRepository mentorShipRepository) {
        this.mentorShipRepository = mentorShipRepository;
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
}
