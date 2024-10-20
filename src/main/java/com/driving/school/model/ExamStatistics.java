package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EXAMSTATISTICS")
public class ExamStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    // ogolna ilosc egzaminów wszystkich studentów zrobiona w danej kategori
    // (do zliczania potem ogolnych statysytk egzaminu)
    // (odswiezane codziennie np. 24.00)
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

    @CreatedDate
    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;
}
