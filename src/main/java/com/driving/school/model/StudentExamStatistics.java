package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "STUDENTEXAMSTATISTICS")
public class StudentExamStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CATEGORY", length = 128)
    private String category;

    @Column(name = "AVERAGEPOINTS")
    private Double averagePoints;

    @Column(name = "NUMBEROFSOLVED")
    private Integer numberOfSolvedExams;

    @Column(name = "NUMBEROFPASSED")
    private Integer numberOfPassedExams;

    @Column(name = "AVERAGEEXAMDURATION")
    private Double averageExamsDuration;

    @Column(name = "AVERAGETIMEPERQUESTION")
    private Double averageTimePerQuestions;

    @Column(name = "NUMBEROFQUESTIONSANSWEREDCORRECTLY", nullable = false)
    private Integer numberOfQuestionsAnsweredCorrectly;

    @Column(name = "NUMBEROFQUESTIONSANSWEREDINCORRECTLY", nullable = false)
    private Integer numberOfQuestionsAnsweredInCorrectly;

    @Column(name = "NUMBEROFQUESTIONSSKIPPED", nullable = false)
    private Integer numberOfQuestionsSkipped;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;
}