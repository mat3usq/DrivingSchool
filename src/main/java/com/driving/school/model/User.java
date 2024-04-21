package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "User", indexes = {
        @Index(name = "user__idx", columnList = "roleId")
})
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "surname", length = 64)
    private String surname;

    @Column(name = "password", length = 256)
    private String password;

    @Column(name = "email", length = 64, unique = true)
    private String email;

    @CreatedDate
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "createdBy", length = 64)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "lastUpdatedAt")
    private LocalDateTime lastUpdatedAt;

    @LastModifiedBy
    @Column(name = "lastUpdatedBy", length = 64)
    private String lastUpdatedBy;

    @OneToMany(mappedBy = "user")
    private Set<Role> roles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<DrivingLesson> drivingLessons = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<LectureMeeting> lectureMeetings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Payment> payments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<SensitiveData> sensitiveData = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<StudentCourse> studentCourses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<StudentExam> studentExams = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserStatistic> userStatistics = new LinkedHashSet<>();
}