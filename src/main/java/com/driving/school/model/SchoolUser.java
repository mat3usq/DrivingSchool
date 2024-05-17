package com.driving.school.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SCHOOLUSER")
public class SchoolUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 64)
    private String name;

    @Column(name = "SURNAME", length = 64)
    private String surname;

    @Column(name = "PASSWORD", length = 256)
    private String password;

    @Column(name = "EMAIL", length = 64)
    private String email;

    @Column(name ="INSTRUCTOR")
    private Long instructor;

    @CreatedDate
    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "CREATEDBY", length = 64)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "LASTUPDATEDAT")
    private LocalDateTime lastUpdatedAt;

    @LastModifiedBy
    @Column(name = "LASTUPDATEDBY", length = 64)
    private String lastUpdatedBy;

    @Column(name = "ROLENAME", length = 128)
    private String roleName;

    @OneToMany(mappedBy = "schoolUser")
    private Set<DrivingLesson> drivingLessons = new LinkedHashSet<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<Lecturemeeting> lectureMeetings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<Payment> payments = new LinkedHashSet<>();

    @OneToOne(mappedBy = "schoolUser")
    private SensitiveData sensitiveData;

    @OneToMany(mappedBy = "schoolUser")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<StudentCourse> studentCourses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<StudentExam> studentExams = new LinkedHashSet<>();

    @OneToOne(mappedBy = "schoolUser")
    private UserStatistic userStatistic;

    public SchoolUser(String name, String surname, String password, String email, String roleName) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.roleName = roleName;
    }
}