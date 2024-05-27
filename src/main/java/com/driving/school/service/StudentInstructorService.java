package com.driving.school.service;


import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentInstructor;
import com.driving.school.repository.StudentInstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
