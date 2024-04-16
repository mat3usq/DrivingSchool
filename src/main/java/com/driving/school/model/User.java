package com.driving.school.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "User", indexes = {
        @Index(name = "user__idx", columnList = "roleId")
})
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

    @Column(name = "email", length = 64)
    private String email;

    @Column(name = "createdAt")
    private LocalDate createdAt;

    @Column(name = "createdBy", length = 64)
    private String createdBy;

    @Column(name = "lastUpdateAt")
    private LocalDate lastUpdateAt;

    @Column(name = "lastUpdatedAt", length = 64)
    private String lastUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;

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