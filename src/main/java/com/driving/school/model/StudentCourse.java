package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "STUDENTCOURSE")
public class StudentCourse {
    @EmbeddedId
    private StudentCourseId id;

    @MapsId("schooluserid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

    @MapsId("courseid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "COURSEID", nullable = false)
    private Course course;

    @Column(name = "PASSED", nullable = false)
    private Long passed;

    @Column(name = "STARTEDAT")
    private LocalDate startedAt;

    @Column(name = "ENDAT")
    private LocalDate endAt;

}