package com.driving.school.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "serviceStastic")
public class ServiceStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "numberSolvedExams")
    private Integer numberSolvedExams;

    @Column(name = "numberAnsweredQuestions")
    private Integer numberAnsweredQuestions;

    @Column(name = "numberStudents")
    private Integer numberStudents;

    @Column(name = "numberCategories")
    private Integer numberCategories;

    @Column(name = "numberInstructors")
    private Integer numberInstructors;

    @Column(name = "createdAt")
    private LocalDate createdAt;
}