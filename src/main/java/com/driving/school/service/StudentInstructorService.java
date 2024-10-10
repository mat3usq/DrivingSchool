package com.driving.school.service;


import com.driving.school.model.Constants;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import com.driving.school.repository.StudentInstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentInstructorService {
    @Autowired
    StudentInstructorRepository studentInstructorRepository;

    public StudentInstructorService(StudentInstructorRepository studentInstructorRepository) {
        this.studentInstructorRepository = studentInstructorRepository;
    }
    public List<StudentInstructor> findByStudentId(Long studentId) {
        return studentInstructorRepository.findByStudentId(studentId);
    }

    public List<StudentInstructor> findByInstructorId(Long instructorId) {
        return studentInstructorRepository.findByInstructorId(instructorId);
    }

    public StudentInstructor findByStudentIdAndInstructorId(Long studentId, Long instructorId) {
        return studentInstructorRepository.findByStudentIdAndInstructorId(studentId, instructorId);
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

    public void createStudentInstructorWithStatus(SchoolUser student, SchoolUser instructor, String status) {
        if (!studentInstructorRepository.existsByStudentAndInstructor(student, instructor)) {
            StudentInstructor studentInstructor = new StudentInstructor();
            studentInstructor.setStudent(student);
            studentInstructor.setInstructor(instructor);
            studentInstructor.setStatus(status);
            studentInstructorRepository.save(studentInstructor);
        }
    }

    public void deleteStudentInstructor(Long studentId, Long instructorId) {
        StudentInstructor studentInstructor = studentInstructorRepository.findByStudentIdAndInstructorId(studentId, instructorId);
        if (studentInstructor != null)
            studentInstructorRepository.delete(studentInstructor);
    }

    public void acceptStudent(Long studentId, Long instructorId) {
        StudentInstructor studentInstructor = studentInstructorRepository.findByStudentIdAndInstructorId(studentId, instructorId);
        studentInstructor.setStatus(Constants.ACTIVE);
        studentInstructorRepository.save(studentInstructor);
    }

    public void finishStudentInstructor(Long studentId, Long instructorId) {
        StudentInstructor studentInstructor = studentInstructorRepository.findByStudentIdAndInstructorId(studentId, instructorId);
        studentInstructor.setStatus(Constants.COMPLETED);
        studentInstructor.setEndAt(LocalDateTime.now());
        studentInstructorRepository.save(studentInstructor);
    }
}
