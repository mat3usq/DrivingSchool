package com.driving.school.service;


import com.driving.school.model.Constants;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.repository.StudentInstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentInstructorService {
    @Autowired
    StudentInstructorRepository studentInstructorRepository;
    @Autowired
    private SchoolUserRepository schoolUserRepository;

    public StudentInstructorService(StudentInstructorRepository studentInstructorRepository) {
        this.studentInstructorRepository = studentInstructorRepository;
    }

    public List<StudentInstructor> findByStudentId(Long studentId) {
        return studentInstructorRepository.findByStudentIdOrderByStatusAsc(studentId);
    }

    public Page<StudentInstructor> findByInstructorId(Long instructorId, Pageable pageable) {
        return studentInstructorRepository.findByInstructorIdOrderByStatusAsc(instructorId, pageable);
    }

    public boolean existsByStudentAndInstructor(SchoolUser student, SchoolUser instructor) {
        return studentInstructorRepository.existsByStudentAndInstructor(student, instructor);
    }

    public StudentInstructor createStudentInstructor(StudentInstructor studentInstructor) {
        return studentInstructorRepository.save(studentInstructor);
    }

    public Optional<StudentInstructor> getStudentInstructorById(Long id) {
        return studentInstructorRepository.findById(id);
    }

    public List<StudentInstructor> getAllStudentInstructors() {
        return studentInstructorRepository.findAll();
    }

    public StudentInstructor updateStudentInstructor(Long id, StudentInstructor studentInstructorDetails) {
        Optional<StudentInstructor> optionalStudentInstructor = studentInstructorRepository.findById(id);

        if (optionalStudentInstructor.isPresent()) {
            StudentInstructor studentInstructor = optionalStudentInstructor.get();
            studentInstructor.setStudent(studentInstructorDetails.getStudent());
            studentInstructor.setInstructor(studentInstructorDetails.getInstructor());
            return studentInstructorRepository.save(studentInstructor);
        } else {
            throw new RuntimeException("StudentInstructor not found with id " + id);
        }
    }

    public void deleteStudentInstructor(Long id) {
        studentInstructorRepository.deleteById(id);
    }

    public boolean createStudentInstructorWithStatus(SchoolUser student, SchoolUser instructor, String status) {
        boolean existsInProgress = studentInstructorRepository.existsByStudentAndInstructorAndStatus(student, instructor, Constants.ACTIVE);
        boolean existsInPending = studentInstructorRepository.existsByStudentAndInstructorAndStatus(student, instructor, Constants.PENDING);

        if (!existsInProgress && !existsInPending) {
            StudentInstructor studentInstructor = new StudentInstructor();
            studentInstructor.setStudent(student);
            studentInstructor.setInstructor(instructor);
            studentInstructor.setStatus(status);
            studentInstructorRepository.save(studentInstructor);

            return true;
        }

        return false;
    }

    public void acceptStudent(Long studentInstructorId) {
        Optional<StudentInstructor> opt = studentInstructorRepository.findById(studentInstructorId);
        if (opt.isPresent()) {
            StudentInstructor studentInstructor = opt.get();
            studentInstructor.setStatus(Constants.ACTIVE);
            studentInstructorRepository.save(studentInstructor);
        }
    }

    public void finishStudentInstructor(Long studentInstructorId) {
        Optional<StudentInstructor> opt = studentInstructorRepository.findById(studentInstructorId);
        if (opt.isPresent()) {
            StudentInstructor studentInstructor = opt.get();
            studentInstructor.setStatus(Constants.COMPLETED);
            studentInstructor.setEndAt(LocalDateTime.now());
            studentInstructorRepository.save(studentInstructor);
        }
    }
}
