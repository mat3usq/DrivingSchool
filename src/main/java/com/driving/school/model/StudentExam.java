package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "STUDENTEXAM")
public class StudentExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CATEGORY", length = 128)
    private String category;

    @Column(name = "POINTS")
    private Long points;

    @Column(name = "PASSED")
    private boolean passed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

    @OneToMany(mappedBy = "studentExam")
    private Set<StudentExamAnswer> studentExamAnswers = new LinkedHashSet<>();

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "EXAMDURATION")
    private Duration examDuration;

    @Column(name = "AVERAGETIMEPERQUESTION")
    private Double averageTimePerQuestion;

    @Column(name = "AMOUNTCORRECTNOSPECANSWERS")
    private long amountCorrectNoSpecAnswers;

    @Column(name = "AMOUNTCORRECTSPECANSWERS")
    private long amountCorrectSpecAnswers;

    @Column(name = "AMOUNTSKIPPEDQUESTIONS")
    private long amountSkippedQuestions;

    @Override
    public String toString() {
        return "StudentExam{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", points=" + points +
                '}';
    }
}