package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "SERVICESTATISTIC")
public class ServiceStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NUMBERSOLVEDEXAMS")
    private Long numberSolvedExams;

    @Column(name = "NUMBERANSWEREDQUESTIONS")
    private Long numberAnsweredQuestions;

    @Column(name = "NUMBERSTUDENT")
    private Long numberStudent;

    @Column(name = "NUMBERCATEGORIES")
    private Long numberCategories;

    @Column(name = "NUMBERINSTRUCTORS")
    private Long numberInstructors;

    @Column(name = "CREATEDAT")
    private LocalDate createdAt;

}