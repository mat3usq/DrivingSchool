package com.driving.school.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "drivingLesson")
public class DrivingLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "startTime")
    private LocalDate startTime;

    @Column(name = "endTime")
    private LocalDate endTime;

    @Column(name = "time", length = 100)
    private String time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "studentId")
    private Integer student;

    @Column(name = "status", length = 128)
    private String status;
}